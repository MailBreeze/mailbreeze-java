package com.mailbreeze.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.models.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Lists Resource")
class ListsTest {

    private MockWebServer mockServer;
    private MailBreezeHttpClient httpClient;
    private Lists lists;
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

        lists = new Lists(httpClient);
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Nested
    @DisplayName("create()")
    class CreateTests {

        @Test
        @DisplayName("should create a contact list")
        void shouldCreateList() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {
                            "success": true,
                            "data": {
                                "id": "list_abc123",
                                "name": "Newsletter Subscribers",
                                "description": "People who signed up for our newsletter",
                                "contact_count": 0,
                                "created_at": "2024-01-15T10:30:00Z"
                            }
                        }
                        """)
                    .setHeader("Content-Type", "application/json"));

            CreateListParams params = CreateListParams.builder()
                    .name("Newsletter Subscribers")
                    .description("People who signed up for our newsletter")
                    .build();

            ContactList result = lists.create(params);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("list_abc123");
            assertThat(result.getName()).isEqualTo("Newsletter Subscribers");
            assertThat(result.getDescription()).isEqualTo("People who signed up for our newsletter");
            assertThat(result.getContactCount()).isEqualTo(0);

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("POST");
            assertThat(request.getPath()).isEqualTo("/contact-lists");
        }

        @Test
        @DisplayName("should create list with custom fields")
        void shouldCreateListWithCustomFields() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {
                            "success": true,
                            "data": {"id": "list_123", "name": "Customers"}
                        }
                        """)
                    .setHeader("Content-Type", "application/json"));

            CreateListParams params = CreateListParams.builder()
                    .name("Customers")
                    .customField(CustomFieldDefinition.builder()
                            .key("company")
                            .label("Company Name")
                            .type(CustomFieldDefinition.FieldType.TEXT)
                            .build())
                    .customField(CustomFieldDefinition.builder()
                            .key("plan")
                            .label("Subscription Plan")
                            .type(CustomFieldDefinition.FieldType.SELECT)
                            .options("free", "pro", "enterprise")
                            .build())
                    .build();

            lists.create(params);

            RecordedRequest request = mockServer.takeRequest();
            String body = request.getBody().readUtf8();
            assertThat(body).contains("company");
            assertThat(body).contains("Company Name");
            assertThat(body).contains("plan");
            assertThat(body).contains("free");
        }
    }

    @Nested
    @DisplayName("list()")
    class ListTests {

        @Test
        @DisplayName("should list contact lists with pagination")
        void shouldListLists() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {
                            "success": true,
                            "data": {
                                "items": [
                                    {"id": "list_1", "name": "Newsletter", "contact_count": 1500},
                                    {"id": "list_2", "name": "Customers", "contact_count": 350}
                                ],
                                "pagination": {
                                    "page": 1,
                                    "limit": 10,
                                    "total": 5,
                                    "total_pages": 1,
                                    "has_next": false,
                                    "has_prev": false
                                }
                            }
                        }
                        """)
                    .setHeader("Content-Type", "application/json"));

            PaginatedResponse<ContactList> response = lists.list();

            assertThat(response).isNotNull();
            assertThat(response.getItems()).hasSize(2);
            assertThat(response.getItems().get(0).getName()).isEqualTo("Newsletter");
            assertThat(response.getItems().get(0).getContactCount()).isEqualTo(1500);

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("GET");
            assertThat(request.getPath()).isEqualTo("/contact-lists");
        }

        @Test
        @DisplayName("should list with search filter")
        void shouldListWithSearch() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {
                            "success": true,
                            "data": {
                                "items": [{"id": "list_1", "name": "Newsletter"}],
                                "pagination": {"page": 1, "limit": 10, "total": 1, "total_pages": 1, "has_next": false, "has_prev": false}
                            }
                        }
                        """)
                    .setHeader("Content-Type", "application/json"));

            ListListsParams params = ListListsParams.builder()
                    .search("news")
                    .limit(10)
                    .build();

            lists.list(params);

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getPath()).contains("search=news");
            assertThat(request.getPath()).contains("limit=10");
        }
    }

    @Nested
    @DisplayName("get()")
    class GetTests {

        @Test
        @DisplayName("should get contact list by ID")
        void shouldGetListById() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {
                            "success": true,
                            "data": {
                                "id": "list_xyz789",
                                "name": "VIP Customers",
                                "description": "Our most valued customers",
                                "contact_count": 250,
                                "custom_fields": [
                                    {"key": "tier", "label": "Tier", "type": "select"}
                                ]
                            }
                        }
                        """)
                    .setHeader("Content-Type", "application/json"));

            ContactList list = lists.get("list_xyz789");

            assertThat(list).isNotNull();
            assertThat(list.getId()).isEqualTo("list_xyz789");
            assertThat(list.getName()).isEqualTo("VIP Customers");
            assertThat(list.getContactCount()).isEqualTo(250);
            assertThat(list.getCustomFields()).hasSize(1);
            assertThat(list.getCustomFields().get(0).getKey()).isEqualTo("tier");

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("GET");
            assertThat(request.getPath()).isEqualTo("/contact-lists/list_xyz789");
        }
    }

    @Nested
    @DisplayName("update()")
    class UpdateTests {

        @Test
        @DisplayName("should update contact list")
        void shouldUpdateList() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {
                            "success": true,
                            "data": {
                                "id": "list_123",
                                "name": "Updated Name",
                                "description": "New description"
                            }
                        }
                        """)
                    .setHeader("Content-Type", "application/json"));

            UpdateListParams params = UpdateListParams.builder()
                    .name("Updated Name")
                    .description("New description")
                    .build();

            ContactList result = lists.update("list_123", params);

            assertThat(result.getName()).isEqualTo("Updated Name");
            assertThat(result.getDescription()).isEqualTo("New description");

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("PATCH");
            assertThat(request.getPath()).isEqualTo("/contact-lists/list_123");
        }
    }

    @Nested
    @DisplayName("delete()")
    class DeleteTests {

        @Test
        @DisplayName("should delete contact list")
        void shouldDeleteList() throws Exception {
            mockServer.enqueue(new MockResponse().setResponseCode(204));

            lists.delete("list_123");

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("DELETE");
            assertThat(request.getPath()).isEqualTo("/contact-lists/list_123");
        }
    }

    @Nested
    @DisplayName("stats()")
    class StatsTests {

        @Test
        @DisplayName("should get list statistics")
        void shouldGetStats() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {
                            "success": true,
                            "data": {
                                "total_contacts": 1000,
                                "active_contacts": 850,
                                "unsubscribed_contacts": 100,
                                "bounced_contacts": 30,
                                "complained_contacts": 10,
                                "suppressed_contacts": 10
                            }
                        }
                        """)
                    .setHeader("Content-Type", "application/json"));

            ListStats stats = lists.stats("list_123");

            assertThat(stats).isNotNull();
            assertThat(stats.getTotalContacts()).isEqualTo(1000);
            assertThat(stats.getActiveContacts()).isEqualTo(850);
            assertThat(stats.getUnsubscribedContacts()).isEqualTo(100);
            assertThat(stats.getBouncedContacts()).isEqualTo(30);
            assertThat(stats.getComplainedContacts()).isEqualTo(10);
            assertThat(stats.getSuppressedContacts()).isEqualTo(10);

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("GET");
            assertThat(request.getPath()).isEqualTo("/contact-lists/list_123/stats");
        }
    }
}
