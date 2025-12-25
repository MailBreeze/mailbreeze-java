package com.mailbreeze.resources;

import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.models.*;

/**
 * Resource for managing file attachments.
 *
 * <p>Attachments use a two-step upload flow:</p>
 * <ol>
 *   <li>Call {@link #createUpload(CreateAttachmentUploadParams)} to get a presigned upload URL</li>
 *   <li>Upload the file directly to that URL using an HTTP PUT request</li>
 *   <li>Call {@link #confirm(ConfirmAttachmentParams)} to mark the upload as complete</li>
 * </ol>
 *
 * <p>Example usage:</p>
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
 * // Step 3: Confirm upload
 * Attachment attachment = mailbreeze.attachments().confirm(
 *     ConfirmAttachmentParams.of(upload.getUploadToken())
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
     * <p>After calling this method, upload the file directly to the returned URL
     * using an HTTP PUT request, then call {@link #confirm} to complete the upload.</p>
     *
     * @param params the upload parameters (file name, content type, size)
     * @return the upload result with presigned URL and token
     */
    public CreateAttachmentUploadResult createUpload(CreateAttachmentUploadParams params) {
        return post("/upload", params, CreateAttachmentUploadResult.class, null);
    }

    /**
     * Confirms that a file upload is complete.
     *
     * <p>Call this after successfully uploading the file to the presigned URL.</p>
     *
     * @param params the confirmation parameters (upload token)
     * @return the confirmed attachment
     */
    public Attachment confirm(ConfirmAttachmentParams params) {
        return post("/confirm", params, Attachment.class, null);
    }
}
