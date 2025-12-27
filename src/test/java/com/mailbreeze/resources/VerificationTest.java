package com.mailbreeze.resources;

import static org.assertj.core.api.Assertions.*;

import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.models.*;
import com.mailbreeze.models.enums.VerificationResult;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

@DisplayName("Verification Resource")
class VerificationTest {

  private MockWebServer mockServer;
  private MailBreezeHttpClient httpClient;
  private Verification verification;

  @BeforeEach
  void setUp() throws IOException {
    mockServer = new MockWebServer();
    mockServer.start();

    httpClient =
        new MailBreezeHttpClient(
            "sk_test_123", mockServer.url("/").toString(), Duration.ofSeconds(30), 3);

    verification = new Verification(httpClient);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockServer.shutdown();
  }

  @Nested
  @DisplayName("verify()")
  class VerifyTests {

    @Test
    @DisplayName("should verify single email")
    void shouldVerifySingleEmail() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "email": "user@example.com",
                                "is_valid": true,
                                "result": "valid",
                                "reason": "Email is valid and deliverable",
                                "cached": false,
                                "risk_score": 10
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      VerifyEmailResult result = verification.verify("user@example.com");

      assertThat(result).isNotNull();
      assertThat(result.getEmail()).isEqualTo("user@example.com");
      assertThat(result.isValid()).isTrue();
      assertThat(result.getResult()).isEqualTo(VerificationResult.VALID);
      assertThat(result.getRiskScore()).isEqualTo(10);

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getPath()).isEqualTo("/api/v1/email-verification/single");
    }
  }

  @Nested
  @DisplayName("batch()")
  class BatchTests {

    @Test
    @DisplayName("should start batch verification")
    void shouldStartBatchVerification() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "verificationId": "ver_abc123",
                                "totalEmails": 100,
                                "creditsDeducted": 100,
                                "status": "processing"
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      BatchVerifyResult result =
          verification.batch(BatchVerifyParams.of(List.of("a@example.com", "b@example.com")));

      assertThat(result).isNotNull();
      assertThat(result.getVerificationId()).isEqualTo("ver_abc123");
      assertThat(result.getTotalEmails()).isEqualTo(100);
      assertThat(result.getStatus()).isEqualTo("processing");

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("POST");
      assertThat(request.getPath()).isEqualTo("/api/v1/email-verification/batch");
    }
  }

  @Nested
  @DisplayName("get()")
  class GetTests {

    @Test
    @DisplayName("should get batch verification status")
    void shouldGetBatchStatus() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "verificationId": "ver_abc123",
                                "totalEmails": 100,
                                "status": "completed",
                                "results": {
                                    "clean": ["a@example.com"],
                                    "dirty": [],
                                    "unknown": []
                                }
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      BatchVerifyResult result = verification.get("ver_abc123");

      assertThat(result).isNotNull();
      assertThat(result.getStatus()).isEqualTo("completed");
      assertThat(result.getResults()).isNotNull();
      assertThat(result.getResults().getClean()).hasSize(1);

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getMethod()).isEqualTo("GET");
      assertThat(request.getPath()).contains("/api/v1/email-verification/ver_abc123");
    }
  }

  @Nested
  @DisplayName("stats()")
  class StatsTests {

    @Test
    @DisplayName("should get verification statistics")
    void shouldGetStats() throws Exception {
      mockServer.enqueue(
          new MockResponse()
              .setBody(
                  """
                        {
                            "success": true,
                            "data": {
                                "totalVerified": 5000,
                                "totalValid": 4500,
                                "totalInvalid": 300,
                                "totalUnknown": 50,
                                "totalVerifications": 5000,
                                "validPercentage": 90.0
                            }
                        }
                        """)
              .setHeader("Content-Type", "application/json"));

      VerificationStats stats = verification.stats();

      assertThat(stats).isNotNull();
      assertThat(stats.getTotalVerified()).isEqualTo(5000);
      assertThat(stats.getTotalValid()).isEqualTo(4500);
      assertThat(stats.getTotalInvalid()).isEqualTo(300);
      assertThat(stats.getTotalUnknown()).isEqualTo(50);
      assertThat(stats.getValidPercentage()).isEqualTo(90.0);

      RecordedRequest request = mockServer.takeRequest();
      assertThat(request.getPath()).isEqualTo("/api/v1/email-verification/stats");
    }
  }
}
