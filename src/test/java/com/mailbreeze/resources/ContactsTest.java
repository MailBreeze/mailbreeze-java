package com.mailbreeze.resources;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.models.*;
import com.mailbreeze.models.enums.ContactStatus;
import com.mailbreeze.models.enums.SuppressReason;
import java.io.IOException;
import java.time.Duration;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

@DisplayName("Contacts Resource")
class ContactsTest {

  private MockWebServer mockServer;
  private MailBreezeHttpClient httpClient;
  private Contacts contacts;
  private ObjectMapper objectMapper;

  private static final String LIST_ID = "list_abc123";

  @BeforeEach
  void setUp() throws IOException {
    mockServer = new MockWebServer();
    mockServer.start();

    httpClient =
        new MailBreezeHttpClient(
            "sk_test_123", mockServer.url("/").toString(), Duration.ofSeconds(30), 3);

    // Contacts are list-scoped
    contacts = new Contacts(httpClient, LIST_ID);
    objectMapper = new ObjectMapper();
  }

  @AfterEach
  void tearDown() throws IOException {
    mockServer.shutdown();
  }

  @Nested
  @DisplayName("Path Building")
  class PathBuildingTests {

    @Test
    @DisplayName("should include list ID in path for all requests")
    void shouldIncludeListIdInPath() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "contacts": [],
                                "pagination": {"page": 1, "limit": 10, "total": 0, "total_pages": 0, "has_next": false, "has_prev": false}
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      contacts.list();

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getPath()).startsWith("/api/v1/contact-lists/list_abc123/contacts");
    }
  }

  @Nested
  @DisplayName("create()")
  class CreateTests {

    @Test
    @DisplayName("should create a contact")
    void shouldCreateContact() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "id": "contact_xyz789",
                                "email": "john@example.com",
                                "first_name": "John",
                                "last_name": "Doe",
                                "status": "active"
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      CreateContactParams params =
          CreateContactParams.builder()
              .email("john@example.com")
              .firstName("John")
              .lastName("Doe")
              .build();

      Contact result = contacts.create(params);

      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo("contact_xyz789");
      assertThat(result.getEmail()).isEqualTo("john@example.com");
      assertThat(result.getFirstName()).isEqualTo("John");
      assertThat(result.getStatus()).isEqualTo(ContactStatus.ACTIVE);

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getPath()).isEqualTo("/api/v1/contact-lists/list_abc123/contacts");
    }

    @Test
    @DisplayName("should create contact with custom fields")
    void shouldCreateWithCustomFields() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {"success": true, "data": {"id": "contact_123", "email": "user@example.com"}}
                        """)
              .setHeader("Content-Type", "application/json"));

      CreateContactParams params =
          CreateContactParams.builder()
              .email("user@example.com")
              .customField("company", "Acme Inc")
              .customField("plan", "enterprise")
              .source("website")
              .build();

      contacts.create(params);

      RecordedRequest request = mockServer.takeRequest();
      String body = request.getBody().readUtf8();
      assertThat(body).contains("Acme Inc");
      assertThat(body).contains("enterprise");
      assertThat(body).contains("website");
    }
  }

  @Nested
  @DisplayName("list()")
  class ListTests {

    @Test
    @DisplayName("should list contacts with pagination")
    void shouldListContacts() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "contacts": [
                                    {"id": "contact_1", "email": "alice@example.com", "status": "active"},
                                    {"id": "contact_2", "email": "bob@example.com", "status": "unsubscribed"}
                                ],
                                "pagination": {
                                    "page": 1,
                                    "limit": 10,
                                    "total": 150,
                                    "total_pages": 15,
                                    "has_next": true,
                                    "has_prev": false
                                }
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      Contacts.ContactsResponse response = contacts.list();

      assertThat(response).isNotNull();
      assertThat(response.getContacts()).hasSize(2);
      assertThat(response.getContacts().get(0).getEmail()).isEqualTo("alice@example.com");
      assertThat(response.getContacts().get(1).getStatus()).isEqualTo(ContactStatus.UNSUBSCRIBED);
      assertThat(response.getPagination().getTotal()).isEqualTo(150);

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("GET");
      assertThat(request.getPath()).isEqualTo("/api/v1/contact-lists/list_abc123/contacts");
    }

    @Test
    @DisplayName("should list contacts with filters")
    void shouldListWithFilters() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "contacts": [],
                                "pagination": {"page": 2, "limit": 25, "total": 0, "total_pages": 0, "has_next": false, "has_prev": true}
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      ListContactsParams params =
          ListContactsParams.builder()
              .status(ContactStatus.ACTIVE)
              .search("john")
              .page(2)
              .limit(25)
              .build();

      contacts.list(params);

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getPath()).contains("status=active");
      assertThat(request.getPath()).contains("search=john");
      assertThat(request.getPath()).contains("page=2");
      assertThat(request.getPath()).contains("limit=25");
    }
  }

  @Nested
  @DisplayName("get()")
  class GetTests {

    @Test
    @DisplayName("should get contact by ID")
    void shouldGetContactById() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "id": "contact_xyz789",
                                "email": "jane@example.com",
                                "first_name": "Jane",
                                "last_name": "Smith",
                                "status": "active",
                                "custom_fields": {"tier": "gold"}
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      Contact contact = contacts.get("contact_xyz789");

      assertThat(contact).isNotNull();
      assertThat(contact.getId()).isEqualTo("contact_xyz789");
      assertThat(contact.getEmail()).isEqualTo("jane@example.com");
      assertThat(contact.getCustomFields()).containsEntry("tier", "gold");

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("GET");
      assertThat(request.getPath())
          .isEqualTo("/api/v1/contact-lists/list_abc123/contacts/contact_xyz789");
    }
  }

  @Nested
  @DisplayName("update()")
  class UpdateTests {

    @Test
    @DisplayName("should update contact")
    void shouldUpdateContact() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "id": "contact_123",
                                "email": "updated@example.com",
                                "first_name": "Updated"
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      UpdateContactParams params =
          UpdateContactParams.builder().email("updated@example.com").firstName("Updated").build();

      Contact result = contacts.update("contact_123", params);

      assertThat(result.getEmail()).isEqualTo("updated@example.com");
      assertThat(result.getFirstName()).isEqualTo("Updated");

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("PUT");
      assertThat(request.getPath())
          .isEqualTo("/api/v1/contact-lists/list_abc123/contacts/contact_123");
    }
  }

  @Nested
  @DisplayName("delete()")
  class DeleteTests {

    @Test
    @DisplayName("should delete contact")
    void shouldDeleteContact() throws Exception {
      mockServer.enqueue(new MockResponse().setResponseCode(204));

      contacts.delete("contact_123");

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("DELETE");
      assertThat(request.getPath())
          .isEqualTo("/api/v1/contact-lists/list_abc123/contacts/contact_123");
    }
  }

  @Nested
  @DisplayName("suppress()")
  class SuppressTests {

    @Test
    @DisplayName("should suppress contact with reason")
    void shouldSuppressContact() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {"success": true, "data": null}
                        """)
              .setHeader("Content-Type", "application/json"));

      contacts.suppress("contact_123", SuppressReason.MANUAL);

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getPath())
          .isEqualTo("/api/v1/contact-lists/list_abc123/contacts/contact_123/suppress");

      String body = request.getBody().readUtf8();
      assertThat(body).contains("manual");
    }
  }
}
