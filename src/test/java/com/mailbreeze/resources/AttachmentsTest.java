package com.mailbreeze.resources;

import static org.assertj.core.api.Assertions.*;

import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.models.*;
import com.mailbreeze.models.enums.AttachmentStatus;
import java.io.IOException;
import java.time.Duration;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

@DisplayName("Attachments Resource")
class AttachmentsTest {

  private MockWebServer mockServer;
  private MailBreezeHttpClient httpClient;
  private Attachments attachments;

  @BeforeEach
  void setUp() throws IOException {
    mockServer = new MockWebServer();
    mockServer.start();

    httpClient =
        new MailBreezeHttpClient(
            "sk_test_123", mockServer.url("/").toString(), Duration.ofSeconds(30), 3);

    attachments = new Attachments(httpClient);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockServer.shutdown();
  }

  @Nested
  @DisplayName("createUpload()")
  class CreateUploadTests {

    @Test
    @DisplayName("should create upload and return presigned URL")
    void shouldCreateUpload() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "attachmentId": "attach_abc123",
                                "uploadUrl": "https://storage.example.com/upload?token=xyz",
                                "expiresAt": "2024-01-15T12:00:00Z"
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      CreateAttachmentUploadParams params =
          CreateAttachmentUploadParams.builder()
              .fileName("report.pdf")
              .contentType("application/pdf")
              .fileSize(1024 * 1024)
              .build();

      CreateAttachmentUploadResult result = attachments.createUpload(params);

      assertThat(result).isNotNull();
      assertThat(result.getAttachmentId()).isEqualTo("attach_abc123");
      assertThat(result.getUploadUrl()).isEqualTo("https://storage.example.com/upload?token=xyz");
      // getUploadToken() returns attachmentId for convenience
      assertThat(result.getUploadToken()).isEqualTo("attach_abc123");

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getPath()).isEqualTo("/api/v1/attachments/presigned-url");
    }
  }

  @Nested
  @DisplayName("confirm()")
  class ConfirmTests {

    @Test
    @DisplayName("should confirm upload and return attachment")
    void shouldConfirmUpload() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "id": "attach_abc123",
                                "file_name": "report.pdf",
                                "content_type": "application/pdf",
                                "file_size": 1048576,
                                "status": "uploaded"
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      ConfirmAttachmentParams params = ConfirmAttachmentParams.of("token_xyz789");

      Attachment result = attachments.confirm(params);

      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo("attach_abc123");
      assertThat(result.getFileName()).isEqualTo("report.pdf");
      assertThat(result.getStatus()).isEqualTo(AttachmentStatus.UPLOADED);

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getPath()).isEqualTo("/api/v1/attachments/token_xyz789/confirm");
    }
  }
}
