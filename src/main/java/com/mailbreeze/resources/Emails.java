package com.mailbreeze.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.http.RequestOptions;
import com.mailbreeze.models.*;
import java.util.List;
import java.util.Map;

/**
 * Resource for sending and managing emails.
 *
 * <p>Example usage:
 *
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
 * EmailsResponse emails = mailbreeze.emails().list(
 *     ListEmailsParams.builder()
 *         .status(EmailStatus.DELIVERED)
 *         .limit(50)
 *         .build()
 * );
 * for (Email e : emails.getEmails()) {
 *     System.out.println(e.getMessageId());
 * }
 *
 * // Cancel a pending email
 * CancelEmailResult cancelled = mailbreeze.emails().cancel("email_123");
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
   * <p>Use an idempotency key to ensure the email is only sent once, even if you retry the request
   * due to network issues.
   *
   * @param params the email parameters
   * @param idempotencyKey unique key for idempotent requests
   * @return the send result containing the email ID and status
   */
  public SendEmailResult send(SendEmailParams params, String idempotencyKey) {
    RequestOptions options =
        idempotencyKey != null
            ? RequestOptions.builder().idempotencyKey(idempotencyKey).build()
            : null;
    return post("", params, SendEmailResult.class, options);
  }

  /**
   * Lists all emails with default pagination.
   *
   * @return emails response with list and pagination
   */
  public EmailsResponse list() {
    return list(null);
  }

  /**
   * Lists emails with filtering and pagination.
   *
   * @param params filter and pagination parameters
   * @return emails response with list and pagination
   */
  public EmailsResponse list(ListEmailsParams params) {
    Map<String, String> queryParams = params != null ? params.toQueryParams() : null;
    return get("", queryParams, EmailsResponse.class);
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
   * Cancels a pending email.
   *
   * <p>Only emails that haven't been sent yet can be cancelled. Emails that are already sent or
   * delivered cannot be cancelled.
   *
   * @param emailId the email ID to cancel
   * @return the cancel result
   */
  public CancelEmailResult cancel(String emailId) {
    return post("/" + emailId + "/cancel", Map.of(), CancelEmailResult.class, null);
  }

  /**
   * Response wrapper for emails list endpoint. Maps the API response format: {emails: [...],
   * pagination: {...}}
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class EmailsResponse {
    @JsonProperty("emails")
    private List<Email> emails;

    private PaginatedResponse.PaginationMeta pagination;

    public List<Email> getEmails() {
      return emails;
    }

    public void setEmails(List<Email> emails) {
      this.emails = emails;
    }

    public PaginatedResponse.PaginationMeta getPagination() {
      return pagination;
    }

    public void setPagination(PaginatedResponse.PaginationMeta pagination) {
      this.pagination = pagination;
    }
  }

  /** Result of cancelling an email. */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class CancelEmailResult {
    private String id;
    private boolean cancelled;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public boolean isCancelled() {
      return cancelled;
    }

    public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
    }
  }
}
