package com.mailbreeze.resources;

import static org.assertj.core.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.models.*;
import com.mailbreeze.models.enums.EmailStatus;
import java.io.IOException;
import java.time.Duration;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

@DisplayName("Emails Resource")
class EmailsTest {

  private MockWebServer mockServer;
  private MailBreezeHttpClient httpClient;
  private Emails emails;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() throws IOException {
    mockServer = new MockWebServer();
    mockServer.start();

    httpClient =
        new MailBreezeHttpClient(
            "sk_test_123", mockServer.url("/").toString(), Duration.ofSeconds(30), 3);

    emails = new Emails(httpClient);
    objectMapper = new ObjectMapper();
  }

  @AfterEach
  void tearDown() throws IOException {
    mockServer.shutdown();
  }

  @Nested
  @DisplayName("send()")
  class SendTests {

    @Test
    @DisplayName("should send email and return result")
    void shouldSendEmail() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "id": "email_abc123",
                                "status": "queued",
                                "messageId": "<msg123@mailbreeze.com>"
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      SendEmailParams params =
          SendEmailParams.builder()
              .from("sender@example.com")
              .to("recipient@example.com")
              .subject("Test Subject")
              .html("<p>Hello World</p>")
              .build();

      SendEmailResult result = emails.send(params);

      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo("email_abc123");
      assertThat(result.getStatus()).isEqualTo(EmailStatus.QUEUED);
      assertThat(result.getMessageId()).isEqualTo("<msg123@mailbreeze.com>");

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getPath()).isEqualTo("/api/v1/emails");
    }

    @Test
    @DisplayName("should include idempotency key when provided")
    void shouldIncludeIdempotencyKey() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {"success": true, "data": {"id": "email_123", "status": "queued"}}
                        """)
              .setHeader("Content-Type", "application/json"));

      SendEmailParams params =
          SendEmailParams.builder().from("sender@example.com").to("recipient@example.com").build();

      emails.send(params, "idem_key_12345");

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getHeader("X-Idempotency-Key")).isEqualTo("idem_key_12345");
    }

    @Test
    @DisplayName("should send email with multiple recipients")
    void shouldSendToMultipleRecipients() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {"success": true, "data": {"id": "email_123", "status": "queued"}}
                        """)
              .setHeader("Content-Type", "application/json"));

      SendEmailParams params =
          SendEmailParams.builder()
              .from("sender@example.com")
              .to("alice@example.com", "bob@example.com")
              .subject("Group email")
              .html("<p>Hello everyone</p>")
              .build();

      emails.send(params);

      RecordedRequest request = mockServer.takeRequest();
      String body = request.getBody().readUtf8();
      assertThat(body).contains("alice@example.com");
      assertThat(body).contains("bob@example.com");
    }

    @Test
    @DisplayName("should send email with template")
    void shouldSendWithTemplate() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {"success": true, "data": {"id": "email_123", "status": "queued"}}
                        """)
              .setHeader("Content-Type", "application/json"));

      SendEmailParams params =
          SendEmailParams.builder()
              .from("sender@example.com")
              .to("recipient@example.com")
              .templateId("tmpl_welcome")
              .variable("name", "John")
              .variable("company", "Acme Inc")
              .build();

      emails.send(params);

      RecordedRequest request = mockServer.takeRequest();
      String body = request.getBody().readUtf8();
      assertThat(body).contains("tmpl_welcome");
      assertThat(body).contains("John");
      assertThat(body).contains("Acme Inc");
    }
  }

  @Nested
  @DisplayName("list()")
  class ListTests {

    @Test
    @DisplayName("should list emails with pagination")
    void shouldListEmails() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "emails": [
                                    {"id": "email_1", "from": "a@example.com", "status": "delivered"},
                                    {"id": "email_2", "from": "b@example.com", "status": "sent"}
                                ],
                                "pagination": {
                                    "page": 1,
                                    "limit": 10,
                                    "total": 25,
                                    "total_pages": 3,
                                    "has_next": true,
                                    "has_prev": false
                                }
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      Emails.EmailsResponse response = emails.list();

      assertThat(response).isNotNull();
      assertThat(response.getEmails()).hasSize(2);
      assertThat(response.getEmails().get(0).getId()).isEqualTo("email_1");
      assertThat(response.getPagination().getTotal()).isEqualTo(25);
      assertThat(response.getPagination().isHasNext()).isTrue();

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("GET");
      assertThat(request.getPath()).isEqualTo("/api/v1/emails");
    }

    @Test
    @DisplayName("should list emails with filter params")
    void shouldListWithFilters() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "emails": [],
                                "pagination": {"page": 2, "limit": 50, "total": 0, "total_pages": 0, "has_next": false, "has_prev": true}
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      ListEmailsParams params =
          ListEmailsParams.builder().status(EmailStatus.DELIVERED).page(2).limit(50).build();

      emails.list(params);

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getPath()).contains("status=delivered");
      assertThat(request.getPath()).contains("page=2");
      assertThat(request.getPath()).contains("limit=50");
    }
  }

  @Nested
  @DisplayName("get()")
  class GetTests {

    @Test
    @DisplayName("should get email by ID")
    void shouldGetEmailById() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "id": "email_xyz789",
                                "from": "sender@example.com",
                                "to": ["recipient@example.com"],
                                "subject": "Hello",
                                "status": "delivered",
                                "messageId": "<msg789@mailbreeze.com>"
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      Email email = emails.get("email_xyz789");

      assertThat(email).isNotNull();
      assertThat(email.getId()).isEqualTo("email_xyz789");
      assertThat(email.getFrom()).isEqualTo("sender@example.com");
      assertThat(email.getSubject()).isEqualTo("Hello");
      assertThat(email.getStatus()).isEqualTo(EmailStatus.DELIVERED);

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("GET");
      assertThat(request.getPath()).isEqualTo("/api/v1/emails/email_xyz789");
    }
  }

  @Nested
  @DisplayName("stats()")
  class StatsTests {

    @Test
    @DisplayName("should get email statistics")
    void shouldGetStats() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "stats": {
                                    "total": 1000,
                                    "sent": 950,
                                    "failed": 50,
                                    "transactional": 600,
                                    "marketing": 400,
                                    "successRate": 95.0
                                }
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      EmailStats stats = emails.stats();

      assertThat(stats).isNotNull();
      assertThat(stats.getTotal()).isEqualTo(1000);
      assertThat(stats.getSent()).isEqualTo(950);
      assertThat(stats.getFailed()).isEqualTo(50);
      assertThat(stats.getTransactional()).isEqualTo(600);
      assertThat(stats.getMarketing()).isEqualTo(400);
      assertThat(stats.getSuccessRate()).isEqualTo(95.0);

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("GET");
      assertThat(request.getPath()).isEqualTo("/api/v1/emails/stats");
    }
  }
}
