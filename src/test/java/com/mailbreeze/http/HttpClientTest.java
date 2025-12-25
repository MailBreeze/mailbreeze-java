package com.mailbreeze.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailbreeze.exceptions.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

@DisplayName("HttpClient")
class HttpClientTest {

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

    @Nested
    @DisplayName("GET Requests")
    class GetRequestTests {

        @Test
        @DisplayName("should send GET request and parse response")
        void shouldSendGetRequest() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {
                            "success": true,
                            "data": {"id": "email_123", "status": "sent"}
                        }
                        """)
                    .setHeader("Content-Type", "application/json")
                    .setHeader("X-Request-Id", "req_abc"));

            JsonNode result = httpClient.get("/emails/email_123", null, JsonNode.class);

            assertThat(result.get("id").asText()).isEqualTo("email_123");
            assertThat(result.get("status").asText()).isEqualTo("sent");

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("GET");
            assertThat(request.getPath()).isEqualTo("/emails/email_123");
        }

        @Test
        @DisplayName("should include query parameters in GET request")
        void shouldIncludeQueryParams() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {"success": true, "data": {"items": []}}
                        """)
                    .setHeader("Content-Type", "application/json"));

            Map<String, String> params = Map.of("page", "2", "limit", "50");
            httpClient.get("/emails", params, JsonNode.class);

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getPath()).contains("page=2");
            assertThat(request.getPath()).contains("limit=50");
        }
    }

    @Nested
    @DisplayName("POST Requests")
    class PostRequestTests {

        @Test
        @DisplayName("should send POST request with JSON body")
        void shouldSendPostRequest() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(201)
                    .setBody("""
                        {
                            "success": true,
                            "data": {"id": "email_456", "status": "pending"}
                        }
                        """)
                    .setHeader("Content-Type", "application/json"));

            Map<String, Object> body = Map.of(
                    "from", "test@example.com",
                    "to", "user@example.com"
            );

            JsonNode result = httpClient.post("/emails", body, JsonNode.class, null);

            assertThat(result.get("id").asText()).isEqualTo("email_456");

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("POST");
            assertThat(request.getHeader("Content-Type")).startsWith("application/json");

            String requestBody = request.getBody().readUtf8();
            assertThat(requestBody).contains("from");
            assertThat(requestBody).contains("test@example.com");
        }

        @Test
        @DisplayName("should include idempotency key when provided")
        void shouldIncludeIdempotencyKey() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {"success": true, "data": {"id": "email_789"}}
                        """));

            RequestOptions options = RequestOptions.builder()
                    .idempotencyKey("unique-key-123")
                    .build();

            httpClient.post("/emails", Map.of(), JsonNode.class, options);

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getHeader("X-Idempotency-Key")).isEqualTo("unique-key-123");
        }

        @Test
        @DisplayName("should prevent header injection in idempotency key")
        void shouldPreventHeaderInjection() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {"success": true, "data": {}}
                        """));

            RequestOptions options = RequestOptions.builder()
                    .idempotencyKey("key\r\nEvil-Header: evil-value")
                    .build();

            httpClient.post("/emails", Map.of(), JsonNode.class, options);

            RecordedRequest request = mockServer.takeRequest();
            // Idempotency key should be sanitized or rejected
            String idempotencyKey = request.getHeader("X-Idempotency-Key");
            if (idempotencyKey != null) {
                assertThat(idempotencyKey).doesNotContain("\r");
                assertThat(idempotencyKey).doesNotContain("\n");
            }
        }
    }

    @Nested
    @DisplayName("PATCH Requests")
    class PatchRequestTests {

        @Test
        @DisplayName("should send PATCH request")
        void shouldSendPatchRequest() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {"success": true, "data": {"id": "lst_123", "name": "Updated"}}
                        """));

            JsonNode result = httpClient.patch("/lists/lst_123", Map.of("name", "Updated"), JsonNode.class);

            assertThat(result.get("name").asText()).isEqualTo("Updated");

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("PATCH");
        }
    }

    @Nested
    @DisplayName("DELETE Requests")
    class DeleteRequestTests {

        @Test
        @DisplayName("should send DELETE request and handle 204 No Content")
        void shouldSendDeleteRequest() throws Exception {
            mockServer.enqueue(new MockResponse().setResponseCode(204));

            httpClient.delete("/lists/lst_123");

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("DELETE");
            assertThat(request.getPath()).isEqualTo("/lists/lst_123");
        }
    }

    @Nested
    @DisplayName("Headers")
    class HeaderTests {

        @Test
        @DisplayName("should set required headers")
        void shouldSetRequiredHeaders() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {"success": true, "data": {}}
                        """));

            httpClient.get("/test", null, JsonNode.class);

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getHeader("X-API-Key")).isEqualTo("sk_test_123");
            assertThat(request.getHeader("Content-Type")).isEqualTo("application/json");
            assertThat(request.getHeader("User-Agent")).startsWith("mailbreeze-java/");
        }

        @Test
        @DisplayName("API key should be redacted in toString")
        void apiKeyShouldBeRedacted() {
            String clientString = httpClient.toString();
            assertThat(clientString).doesNotContain("sk_test_123");
            assertThat(clientString).contains("[REDACTED]");
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandlingTests {

        @Test
        @DisplayName("should throw AuthenticationException for 401")
        void shouldThrowAuthExceptionFor401() {
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(401)
                    .setBody("""
                        {
                            "success": false,
                            "error": {"code": "UNAUTHORIZED", "message": "Invalid API key"}
                        }
                        """)
                    .setHeader("X-Request-Id", "req_401"));

            assertThatThrownBy(() -> httpClient.get("/test", null, JsonNode.class))
                    .isInstanceOf(AuthenticationException.class)
                    .hasMessageContaining("Invalid API key");
        }

        @Test
        @DisplayName("should throw ValidationException for 400")
        void shouldThrowValidationExceptionFor400() {
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(400)
                    .setBody("""
                        {
                            "success": false,
                            "error": {
                                "code": "VALIDATION_ERROR",
                                "message": "Invalid email",
                                "details": {"email": "must be valid"}
                            }
                        }
                        """));

            assertThatThrownBy(() -> httpClient.get("/test", null, JsonNode.class))
                    .isInstanceOf(ValidationException.class)
                    .satisfies(e -> {
                        ValidationException ve = (ValidationException) e;
                        assertThat(ve.getDetails()).containsKey("email");
                    });
        }

        @Test
        @DisplayName("should throw NotFoundException for 404")
        void shouldThrowNotFoundExceptionFor404() {
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(404)
                    .setBody("""
                        {
                            "success": false,
                            "error": {"code": "NOT_FOUND", "message": "Email not found"}
                        }
                        """));

            assertThatThrownBy(() -> httpClient.get("/emails/not_found", null, JsonNode.class))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("should throw RateLimitException for 429 with Retry-After")
        void shouldThrowRateLimitExceptionFor429() {
            // Queue enough responses for initial + retries (maxRetries=3)
            for (int i = 0; i < 4; i++) {
                mockServer.enqueue(new MockResponse()
                        .setResponseCode(429)
                        .setHeader("Retry-After", "60")
                        .setBody("""
                            {
                                "success": false,
                                "error": {"code": "RATE_LIMIT_EXCEEDED", "message": "Too many requests"}
                            }
                            """));
            }

            assertThatThrownBy(() -> httpClient.get("/test", null, JsonNode.class))
                    .isInstanceOf(RateLimitException.class)
                    .satisfies(e -> {
                        RateLimitException rle = (RateLimitException) e;
                        assertThat(rle.getRetryAfter()).isEqualTo(60);
                    });
        }

        @Test
        @DisplayName("should throw ServerException for 500")
        void shouldThrowServerExceptionFor500() {
            // Queue enough responses for initial + retries (maxRetries=3)
            for (int i = 0; i < 4; i++) {
                mockServer.enqueue(new MockResponse()
                        .setResponseCode(500)
                        .setBody("""
                            {
                                "success": false,
                                "error": {"code": "SERVER_ERROR", "message": "Internal error"}
                            }
                            """));
            }

            assertThatThrownBy(() -> httpClient.get("/test", null, JsonNode.class))
                    .isInstanceOf(ServerException.class);
        }

        @Test
        @DisplayName("should include requestId in exception")
        void shouldIncludeRequestIdInException() {
            // Queue enough responses for initial + retries (maxRetries=3)
            for (int i = 0; i < 4; i++) {
                mockServer.enqueue(new MockResponse()
                        .setResponseCode(500)
                        .setHeader("X-Request-Id", "req_error_123")
                        .setBody("""
                            {"success": false, "error": {"message": "Error"}}
                            """));
            }

            assertThatThrownBy(() -> httpClient.get("/test", null, JsonNode.class))
                    .isInstanceOf(MailBreezeException.class)
                    .satisfies(e -> {
                        MailBreezeException mbe = (MailBreezeException) e;
                        assertThat(mbe.getRequestId()).isEqualTo("req_error_123");
                    });
        }
    }

    @Nested
    @DisplayName("Retry Logic")
    class RetryLogicTests {

        @Test
        @DisplayName("should retry on 500 error")
        void shouldRetryOn500() throws Exception {
            // First request fails with 500
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(500)
                    .setBody("""
                        {"success": false, "error": {"message": "Temporary error"}}
                        """));

            // Second request succeeds
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {"success": true, "data": {"id": "success"}}
                        """));

            JsonNode result = httpClient.get("/test", null, JsonNode.class);
            assertThat(result.get("id").asText()).isEqualTo("success");
            assertThat(mockServer.getRequestCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("should retry on 429 with Retry-After")
        void shouldRetryOn429() throws Exception {
            // First request rate limited
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(429)
                    .setHeader("Retry-After", "1")
                    .setBody("""
                        {"success": false, "error": {"message": "Rate limited"}}
                        """));

            // Second request succeeds
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {"success": true, "data": {"id": "retry_success"}}
                        """));

            JsonNode result = httpClient.get("/test", null, JsonNode.class);
            assertThat(result.get("id").asText()).isEqualTo("retry_success");
            assertThat(mockServer.getRequestCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("should not retry on 400")
        void shouldNotRetryOn400() {
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(400)
                    .setBody("""
                        {"success": false, "error": {"message": "Bad request"}}
                        """));

            assertThatThrownBy(() -> httpClient.get("/test", null, JsonNode.class))
                    .isInstanceOf(ValidationException.class);

            assertThat(mockServer.getRequestCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("should not retry on 401")
        void shouldNotRetryOn401() {
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(401)
                    .setBody("""
                        {"success": false, "error": {"message": "Unauthorized"}}
                        """));

            assertThatThrownBy(() -> httpClient.get("/test", null, JsonNode.class))
                    .isInstanceOf(AuthenticationException.class);

            assertThat(mockServer.getRequestCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("should respect max retries")
        void shouldRespectMaxRetries() {
            // Queue more failures than max retries
            for (int i = 0; i < 5; i++) {
                mockServer.enqueue(new MockResponse()
                        .setResponseCode(500)
                        .setBody("""
                            {"success": false, "error": {"message": "Server error"}}
                            """));
            }

            assertThatThrownBy(() -> httpClient.get("/test", null, JsonNode.class))
                    .isInstanceOf(ServerException.class);

            // Should have tried initial + 3 retries = 4 total (maxRetries=3)
            assertThat(mockServer.getRequestCount()).isEqualTo(4);
        }
    }

    @Nested
    @DisplayName("Async Support")
    class AsyncTests {

        @Test
        @DisplayName("getAsync should return CompletableFuture")
        void getAsyncShouldReturnFuture() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {"success": true, "data": {"id": "async_123"}}
                        """));

            CompletableFuture<JsonNode> future = httpClient.getAsync("/test", null, JsonNode.class);

            JsonNode result = future.get(5, TimeUnit.SECONDS);
            assertThat(result.get("id").asText()).isEqualTo("async_123");
        }

        @Test
        @DisplayName("postAsync should return CompletableFuture")
        void postAsyncShouldReturnFuture() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(201)
                    .setBody("""
                        {"success": true, "data": {"id": "async_post"}}
                        """));

            CompletableFuture<JsonNode> future = httpClient.postAsync(
                    "/emails", Map.of("test", true), JsonNode.class, null
            );

            JsonNode result = future.get(5, TimeUnit.SECONDS);
            assertThat(result.get("id").asText()).isEqualTo("async_post");
        }

        @Test
        @DisplayName("async should complete exceptionally on error")
        void asyncShouldCompleteExceptionallyOnError() {
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(401)
                    .setBody("""
                        {"success": false, "error": {"message": "Unauthorized"}}
                        """));

            CompletableFuture<JsonNode> future = httpClient.getAsync("/test", null, JsonNode.class);

            assertThatThrownBy(() -> future.get(5, TimeUnit.SECONDS))
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(AuthenticationException.class);
        }

        @Test
        @DisplayName("async should retry on 5xx")
        void asyncShouldRetryOn5xx() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(500)
                    .setBody("""
                        {"success": false, "error": {"message": "Temporary"}}
                        """));

            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {"success": true, "data": {"id": "async_retry"}}
                        """));

            CompletableFuture<JsonNode> future = httpClient.getAsync("/test", null, JsonNode.class);

            JsonNode result = future.get(5, TimeUnit.SECONDS);
            assertThat(result.get("id").asText()).isEqualTo("async_retry");
            assertThat(mockServer.getRequestCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("multiple concurrent async calls should work")
        void multipleConcurrentAsyncCalls() throws Exception {
            for (int i = 0; i < 5; i++) {
                mockServer.enqueue(new MockResponse()
                        .setBody(String.format("""
                            {"success": true, "data": {"id": "email_%d"}}
                            """, i)));
            }

            var futures = new CompletableFuture[5];
            for (int i = 0; i < 5; i++) {
                futures[i] = httpClient.getAsync("/emails/" + i, null, JsonNode.class);
            }

            CompletableFuture.allOf(futures).get(10, TimeUnit.SECONDS);

            for (int i = 0; i < 5; i++) {
                JsonNode result = (JsonNode) futures[i].get();
                assertThat(result.get("id").asText()).startsWith("email_");
            }
        }
    }

    @Nested
    @DisplayName("Response Parsing")
    class ResponseParsingTests {

        @Test
        @DisplayName("should parse data from success response envelope")
        void shouldParseDataFromEnvelope() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {
                            "success": true,
                            "data": {
                                "id": "email_123",
                                "from": "test@example.com",
                                "status": "sent"
                            },
                            "meta": {
                                "requestId": "req_xyz"
                            }
                        }
                        """));

            JsonNode result = httpClient.get("/emails/email_123", null, JsonNode.class);

            // Should return data, not the full envelope
            assertThat(result.has("success")).isFalse();
            assertThat(result.get("id").asText()).isEqualTo("email_123");
            assertThat(result.get("from").asText()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("should handle API error in success=false response")
        void shouldHandleApiErrorResponse() {
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(200) // Sometimes APIs return 200 with success=false
                    .setBody("""
                        {
                            "success": false,
                            "error": {
                                "code": "DOMAIN_NOT_VERIFIED",
                                "message": "Domain not verified"
                            }
                        }
                        """));

            assertThatThrownBy(() -> httpClient.get("/test", null, JsonNode.class))
                    .isInstanceOf(MailBreezeException.class)
                    .hasMessageContaining("Domain not verified");
        }
    }
}
