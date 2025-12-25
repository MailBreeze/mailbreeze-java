package com.mailbreeze.resources;

import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.models.*;

import java.util.Map;

/**
 * Resource for managing automation enrollments.
 *
 * <p>Automations are pre-built email workflows. Use this resource to enroll
 * contacts and manage their enrollments.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Enroll a contact in an automation
 * EnrollResult result = mailbreeze.automations().enroll(
 *     EnrollParams.builder()
 *         .automationId("auto_welcome")
 *         .contactId("contact_123")
 *         .variable("name", "John")
 *         .build()
 * );
 *
 * // List enrollments
 * PaginatedResponse<Enrollment> enrollments = mailbreeze.automations().enrollments().list(
 *     ListEnrollmentsParams.builder()
 *         .automationId("auto_welcome")
 *         .status(EnrollmentStatus.ACTIVE)
 *         .build()
 * );
 *
 * // Cancel an enrollment
 * mailbreeze.automations().enrollments().cancel("enrollment_123");
 * }</pre>
 */
public class Automations extends BaseResource {

    private final AutomationEnrollments enrollments;

    /**
     * Creates a new Automations resource.
     *
     * @param httpClient the HTTP client for making requests
     */
    public Automations(MailBreezeHttpClient httpClient) {
        super(httpClient, "/automations");
        this.enrollments = new AutomationEnrollments(httpClient);
    }

    /**
     * Enrolls a contact in an automation.
     *
     * @param params the enrollment parameters
     * @return the enrollment result
     */
    public EnrollResult enroll(EnrollParams params) {
        return post("/" + params.getAutomationId() + "/enroll", params, EnrollResult.class, null);
    }

    /**
     * Returns the enrollments sub-resource for managing enrollments.
     *
     * @return the enrollments sub-resource
     */
    public AutomationEnrollments enrollments() {
        return enrollments;
    }

    /**
     * Sub-resource for managing automation enrollments.
     */
    public static class AutomationEnrollments extends BaseResource {

        AutomationEnrollments(MailBreezeHttpClient httpClient) {
            super(httpClient, "/automation-enrollments");
        }

        /**
         * Lists automation enrollments.
         *
         * @return paginated list of enrollments
         */
        public PaginatedResponse<Enrollment> list() {
            return list(null);
        }

        /**
         * Lists automation enrollments with filters.
         *
         * @param params filter parameters
         * @return paginated list of enrollments
         */
        @SuppressWarnings("unchecked")
        public PaginatedResponse<Enrollment> list(ListEnrollmentsParams params) {
            Map<String, String> queryParams = params != null ? params.toQueryParams() : null;
            return (PaginatedResponse<Enrollment>) get("", queryParams, EnrollmentPaginatedResponse.class);
        }

        /**
         * Cancels an enrollment.
         *
         * @param enrollmentId the enrollment ID to cancel
         * @return the cancellation result
         */
        public CancelEnrollmentResult cancel(String enrollmentId) {
            return post("/" + enrollmentId + "/cancel", null, CancelEnrollmentResult.class, null);
        }

        /**
         * Concrete type for deserializing paginated enrollment responses.
         */
        public static class EnrollmentPaginatedResponse extends PaginatedResponse<Enrollment> {
        }
    }
}
