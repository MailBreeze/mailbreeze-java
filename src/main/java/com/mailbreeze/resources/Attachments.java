package com.mailbreeze.resources;

import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.models.*;
import java.util.Map;

/**
 * Resource for managing file attachments.
 *
 * <p>Attachments use a two-step upload flow:
 *
 * <ol>
 *   <li>Call {@link #createUpload(CreateAttachmentUploadParams)} to get a presigned upload URL
 *   <li>Upload the file directly to that URL using an HTTP PUT request
 *   <li>Call {@link #confirm(ConfirmAttachmentParams)} to mark the upload as complete
 * </ol>
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * // Step 1: Create upload
 * CreateAttachmentUploadResult upload = mailbreeze.attachments().createUpload(
 *     CreateAttachmentUploadParams.builder()
 *         .fileName("report.pdf")
 *         .contentType("application/pdf")
 *         .fileSize(fileBytes.length)
 *         .build()
 * );
 *
 * // Step 2: Upload file to presigned URL (use your HTTP client)
 * // PUT upload.getUploadUrl() with file content
 *
 * // Step 3: Confirm upload (use attachmentId as the token)
 * Attachment attachment = mailbreeze.attachments().confirm(
 *     ConfirmAttachmentParams.of(upload.getAttachmentId())
 * );
 *
 * // Use attachment ID when sending email
 * mailbreeze.emails().send(
 *     SendEmailParams.builder()
 *         .from("sender@example.com")
 *         .to("recipient@example.com")
 *         .subject("Report attached")
 *         .attachmentIds(List.of(attachment.getId()))
 *         .build()
 * );
 * }</pre>
 */
public class Attachments extends BaseResource {

  /**
   * Creates a new Attachments resource.
   *
   * @param httpClient the HTTP client for making requests
   */
  public Attachments(MailBreezeHttpClient httpClient) {
    super(httpClient, "/attachments");
  }

  /**
   * Creates a presigned URL for uploading an attachment.
   *
   * <p>After calling this method, upload the file directly to the returned URL using an HTTP PUT
   * request, then call {@link #confirm} to complete the upload.
   *
   * @param params the upload parameters (file name, content type, size)
   * @return the upload result with presigned URL and token
   */
  public CreateAttachmentUploadResult createUpload(CreateAttachmentUploadParams params) {
    return post("/presigned-url", params, CreateAttachmentUploadResult.class, null);
  }

  /**
   * Confirms that a file upload is complete.
   *
   * <p>Call this after successfully uploading the file to the presigned URL.
   *
   * @param params the confirmation parameters (upload token)
   * @return the confirmed attachment (may be null if API returns no data)
   */
  public Attachment confirm(ConfirmAttachmentParams params) {
    return post("/" + params.getUploadToken() + "/confirm", Map.of(), Attachment.class, null);
  }

  /**
   * Gets an attachment by ID.
   *
   * @param attachmentId the attachment ID
   * @return the attachment details
   */
  public Attachment get(String attachmentId) {
    return get("/" + attachmentId, null, Attachment.class);
  }

  /**
   * Deletes an attachment.
   *
   * @param attachmentId the attachment ID to delete
   */
  public void delete(String attachmentId) {
    doDelete("/" + attachmentId);
  }
}
