package com.mailbreeze.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.http.RequestOptions;
import com.mailbreeze.models.*;

import java.util.Map;

/**
 * Resource for sending and managing emails.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Send an email
 * SendEmailResult result = mailbreeze.emails().send(
 *     SendEmailParams.builder()
 *         .from("sender@example.com")
 *         .to("recipient@example.com")
 *         .subject("Hello!")
 *         .html("<p>Welcome!</p>")
 *         .build()
 * );
 *
 * // List emails
 * PaginatedResponse<Email> emails = mailbreeze.emails().list(
 *     ListEmailsParams.builder()
 *         .status(EmailStatus.DELIVERED)
 *         .limit(50)
 *         .build()
 * );
 *
 * // Get email by ID
 * Email email = mailbreeze.emails().get("email_123");
 *
 * // Get email statistics
 * EmailStats stats = mailbreeze.emails().stats();
 * }</pre>
 */
public class Emails extends BaseResource {

    /**
     * Creates a new Emails resource.
     *
     * @param httpClient the HTTP client for making requests
     */
    public Emails(MailBreezeHttpClient httpClient) {
        super(httpClient, "/emails");
    }

    /**
     * Sends an email.
     *
     * @param params the email parameters
     * @return the send result containing the email ID and status
     */
    public SendEmailResult send(SendEmailParams params) {
        return send(params, null);
    }

    /**
     * Sends an email with an idempotency key.
     *
     * <p>Use an idempotency key to ensure the email is only sent once,
     * even if you retry the request due to network issues.</p>
     *
     * @param params         the email parameters
     * @param idempotencyKey unique key for idempotent requests
     * @return the send result containing the email ID and status
     */
    public SendEmailResult send(SendEmailParams params, String idempotencyKey) {
        RequestOptions options = idempotencyKey != null
                ? RequestOptions.builder().idempotencyKey(idempotencyKey).build()
                : null;
        return post("", params, SendEmailResult.class, options);
    }

    /**
     * Lists all emails with default pagination.
     *
     * @return paginated list of emails
     */
    public PaginatedResponse<Email> list() {
        return list(null);
    }

    /**
     * Lists emails with filtering and pagination.
     *
     * @param params filter and pagination parameters
     * @return paginated list of emails matching the filters
     */
    @SuppressWarnings("unchecked")
    public PaginatedResponse<Email> list(ListEmailsParams params) {
        Map<String, String> queryParams = params != null ? params.toQueryParams() : null;
        return (PaginatedResponse<Email>) get("", queryParams, EmailPaginatedResponse.class);
    }

    /**
     * Gets an email by ID.
     *
     * @param emailId the email ID
     * @return the email details
     */
    public Email get(String emailId) {
        return get("/" + emailId, null, Email.class);
    }

    /**
     * Gets email sending statistics.
     *
     * @return the email statistics
     */
    public EmailStats stats() {
        EmailStatsResponse response = get("/stats", null, EmailStatsResponse.class);
        return response != null ? response.getStats() : null;
    }

    /**
     * Concrete type for deserializing paginated email responses.
     * Jackson requires a concrete type to properly handle generics.
     */
    public static class EmailPaginatedResponse extends PaginatedResponse<Email> {
    }
}
