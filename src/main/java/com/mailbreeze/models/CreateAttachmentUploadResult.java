package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

/** Result of creating an attachment upload. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateAttachmentUploadResult {

  private String attachmentId;
  private String uploadUrl;
  private Instant expiresAt;

  public String getAttachmentId() {
    return attachmentId;
  }

  public void setAttachmentId(String attachmentId) {
    this.attachmentId = attachmentId;
  }

  public String getUploadUrl() {
    return uploadUrl;
  }

  public void setUploadUrl(String uploadUrl) {
    this.uploadUrl = uploadUrl;
  }

  /**
   * Gets the upload token for confirming the upload. This is the same as the attachment ID.
   *
   * @return the upload token (attachment ID)
   */
  public String getUploadToken() {
    return attachmentId;
  }

  public Instant getExpiresAt() {
    return expiresAt;
  }

  public void setExpiresAt(Instant expiresAt) {
    this.expiresAt = expiresAt;
  }
}
