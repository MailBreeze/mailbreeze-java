package com.mailbreeze.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.http.RequestOptions;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("BaseResource")
class BaseResourceTest {

    private MockWebServer mockServer;
    private MailBreezeHttpClient httpClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();

        httpClient = new MailBreezeHttpClient(
                "sk_test_123",
                mockServer.url("/").toString(),
                Duration.ofSeconds(30),
                3
        );

        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    /**
     * Concrete test implementation of BaseResource for testing purposes.
     */
    static class TestResource extends BaseResource {
        TestResource(MailBreezeHttpClient httpClient) {
            super(httpClient, "/test-items");
        }

        // Expose protected methods for testing
        public <T> T testGet(String path, Map<String, String> queryParams, Class<T> responseType) {
            return get(path, queryParams, responseType);
        }

        public <T> T testPost(String path, Object body, Class<T> responseType, RequestOptions options) {
            return post(path, body, responseType, options);
        }

        public <T> T testPatch(String path, Object body, Class<T> responseType) {
            return patch(path, body, responseType);
        }

        public void testDelete(String path) {
            doDelete(path);
        }

        public String testBuildPath(String path) {
            return buildPath(path);
        }
    }

    @Nested
    @DisplayName("Path Building")
    class PathBuildingTests {

        @Test
        @DisplayName("should prepend base path to relative path")
        void shouldPrependBasePath() {
            TestResource resource = new TestResource(httpClient);

            String path = resource.testBuildPath("/123");

            assertThat(path).isEqualTo("/test-items/123");
        }

        @Test
        @DisplayName("should handle empty path")
        void shouldHandleEmptyPath() {
            TestResource resource = new TestResource(httpClient);

            String path = resource.testBuildPath("");

            assertThat(path).isEqualTo("/test-items");
        }

        @Test
        @DisplayName("should handle path with query indication")
        void shouldHandleComplexPath() {
            TestResource resource = new TestResource(httpClient);

            String path = resource.testBuildPath("/123/nested");

            assertThat(path).isEqualTo("/test-items/123/nested");
        }
    }

    @Nested
    @DisplayName("GET Requests")
    class GetRequestTests {

        @Test
        @DisplayName("should delegate GET request to HTTP client with built path")
        void shouldDelegateGetRequest() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {
                            "success": true,
                            "data": {"id": "item_123", "name": "Test Item"}
                        }
                        """)
                    .setHeader("Content-Type", "application/json"));

            TestResource resource = new TestResource(httpClient);
            JsonNode result = resource.testGet("/item_123", null, JsonNode.class);

            assertThat(result.get("id").asText()).isEqualTo("item_123");
            assertThat(result.get("name").asText()).isEqualTo("Test Item");

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("GET");
            assertThat(request.getPath()).isEqualTo("/test-items/item_123");
        }

        @Test
        @DisplayName("should pass query parameters to HTTP client")
        void shouldPassQueryParams() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {"success": true, "data": {"items": []}}
                        """)
                    .setHeader("Content-Type", "application/json"));

            TestResource resource = new TestResource(httpClient);
            Map<String, String> params = Map.of("page", "2", "limit", "50");
            resource.testGet("", params, JsonNode.class);

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getPath()).contains("page=2");
            assertThat(request.getPath()).contains("limit=50");
        }
    }

    @Nested
    @DisplayName("POST Requests")
    class PostRequestTests {

        @Test
        @DisplayName("should delegate POST request to HTTP client with built path")
        void shouldDelegatePostRequest() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {
                            "success": true,
                            "data": {"id": "item_new", "name": "Created Item"}
                        }
                        """)
                    .setHeader("Content-Type", "application/json"));

            TestResource resource = new TestResource(httpClient);
            Map<String, Object> body = Map.of("name", "Created Item");
            JsonNode result = resource.testPost("", body, JsonNode.class, null);

            assertThat(result.get("id").asText()).isEqualTo("item_new");

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("POST");
            assertThat(request.getPath()).isEqualTo("/test-items");
        }

        @Test
        @DisplayName("should pass request options to HTTP client")
        void shouldPassRequestOptions() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {"success": true, "data": {"id": "item_123"}}
                        """)
                    .setHeader("Content-Type", "application/json"));

            TestResource resource = new TestResource(httpClient);
            RequestOptions options = RequestOptions.builder().idempotencyKey("idem_123").build();
            resource.testPost("", Map.of(), JsonNode.class, options);

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getHeader("X-Idempotency-Key")).isEqualTo("idem_123");
        }
    }

    @Nested
    @DisplayName("PATCH Requests")
    class PatchRequestTests {

        @Test
        @DisplayName("should delegate PATCH request to HTTP client with built path")
        void shouldDelegatePatchRequest() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {
                            "success": true,
                            "data": {"id": "item_123", "name": "Updated Item"}
                        }
                        """)
                    .setHeader("Content-Type", "application/json"));

            TestResource resource = new TestResource(httpClient);
            Map<String, Object> body = Map.of("name", "Updated Item");
            JsonNode result = resource.testPatch("/item_123", body, JsonNode.class);

            assertThat(result.get("name").asText()).isEqualTo("Updated Item");

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("PATCH");
            assertThat(request.getPath()).isEqualTo("/test-items/item_123");
        }
    }

    @Nested
    @DisplayName("DELETE Requests")
    class DeleteRequestTests {

        @Test
        @DisplayName("should delegate DELETE request to HTTP client with built path")
        void shouldDelegateDeleteRequest() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(204));

            TestResource resource = new TestResource(httpClient);
            resource.testDelete("/item_123");

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("DELETE");
            assertThat(request.getPath()).isEqualTo("/test-items/item_123");
        }
    }
}
