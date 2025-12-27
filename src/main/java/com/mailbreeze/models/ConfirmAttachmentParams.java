package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Parameters for confirming an attachment upload. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfirmAttachmentParams {

  @JsonProperty("upload_token")
  private String uploadToken;

  private ConfirmAttachmentParams(String uploadToken) {
    this.uploadToken = uploadToken;
  }

  public static ConfirmAttachmentParams of(String uploadToken) {
    if (uploadToken == null || uploadToken.isBlank()) {
      throw new IllegalArgumentException("uploadToken is required");
    }
    return new ConfirmAttachmentParams(uploadToken);
  }

  public String getUploadToken() {
    return uploadToken;
  }
}
