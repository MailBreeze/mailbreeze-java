package com.mailbreeze.resources;

import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.models.*;
import com.mailbreeze.models.enums.EnrollmentStatus;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Automations Resource")
class AutomationsTest {

    private MockWebServer mockServer;
    private MailBreezeHttpClient httpClient;
    private Automations automations;

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

        automations = new Automations(httpClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    @Nested
    @DisplayName("enroll()")
    class EnrollTests {

        @Test
        @DisplayName("should enroll contact in automation")
        void shouldEnrollContact() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {
                            "success": true,
                            "data": {
                                "enrollment_id": "enroll_abc123",
                                "status": "active",
                                "enrolled_at": "2024-01-15T10:30:00Z"
                            }
                        }
                        """)
                    .setHeader("Content-Type", "application/json"));

            EnrollParams params = EnrollParams.builder()
                    .automationId("auto_welcome")
                    .contactId("contact_123")
                    .variable("name", "John")
                    .build();

            EnrollResult result = automations.enroll(params);

            assertThat(result).isNotNull();
            assertThat(result.getEnrollmentId()).isEqualTo("enroll_abc123");
            assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("POST");
            assertThat(request.getPath()).isEqualTo("/automations/auto_welcome/enroll");
        }
    }

    @Nested
    @DisplayName("enrollments.list()")
    class ListEnrollmentsTests {

        @Test
        @DisplayName("should list enrollments")
        void shouldListEnrollments() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {
                            "success": true,
                            "data": {
                                "items": [
                                    {
                                        "id": "enroll_1",
                                        "automation_id": "auto_welcome",
                                        "automation_name": "Welcome Series",
                                        "contact_email": "user@example.com",
                                        "status": "active",
                                        "current_step": 2,
                                        "total_steps": 5
                                    }
                                ],
                                "pagination": {
                                    "page": 1,
                                    "limit": 10,
                                    "total": 50,
                                    "total_pages": 5,
                                    "has_next": true,
                                    "has_prev": false
                                }
                            }
                        }
                        """)
                    .setHeader("Content-Type", "application/json"));

            PaginatedResponse<Enrollment> response = automations.enrollments().list();

            assertThat(response).isNotNull();
            assertThat(response.getItems()).hasSize(1);
            assertThat(response.getItems().get(0).getAutomationName()).isEqualTo("Welcome Series");
            assertThat(response.getItems().get(0).getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
            assertThat(response.getItems().get(0).getCurrentStep()).isEqualTo(2);

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("GET");
            assertThat(request.getPath()).isEqualTo("/automation-enrollments");
        }

        @Test
        @DisplayName("should list enrollments with filters")
        void shouldListWithFilters() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {
                            "success": true,
                            "data": {
                                "items": [],
                                "pagination": {"page": 1, "limit": 10, "total": 0, "total_pages": 0, "has_next": false, "has_prev": false}
                            }
                        }
                        """)
                    .setHeader("Content-Type", "application/json"));

            ListEnrollmentsParams params = ListEnrollmentsParams.builder()
                    .automationId("auto_welcome")
                    .status(EnrollmentStatus.ACTIVE)
                    .build();

            automations.enrollments().list(params);

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getPath()).contains("automation_id=auto_welcome");
            assertThat(request.getPath()).contains("status=active");
        }
    }

    @Nested
    @DisplayName("enrollments.cancel()")
    class CancelEnrollmentTests {

        @Test
        @DisplayName("should cancel enrollment")
        void shouldCancelEnrollment() throws Exception {
            mockServer.enqueue(new MockResponse()
                    .setBody("""
                        {
                            "success": true,
                            "data": {
                                "enrollment_id": "enroll_123",
                                "status": "cancelled",
                                "cancelled_at": "2024-01-15T12:00:00Z"
                            }
                        }
                        """)
                    .setHeader("Content-Type", "application/json"));

            CancelEnrollmentResult result = automations.enrollments().cancel("enroll_123");

            assertThat(result).isNotNull();
            assertThat(result.getEnrollmentId()).isEqualTo("enroll_123");
            assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);

            RecordedRequest request = mockServer.takeRequest();
            assertThat(request.getMethod()).isEqualTo("POST");
            assertThat(request.getPath()).isEqualTo("/automation-enrollments/enroll_123/cancel");
        }
    }
}
