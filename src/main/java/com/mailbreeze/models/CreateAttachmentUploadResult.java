package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Result of creating an attachment upload.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateAttachmentUploadResult {

    @JsonProperty("attachment_id")
    private String attachmentId;

    @JsonProperty("upload_url")
    private String uploadUrl;

    @JsonProperty("upload_token")
    private String uploadToken;

    @JsonProperty("expires_at")
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

    public String getUploadToken() {
        return uploadToken;
    }

    public void setUploadToken(String uploadToken) {
        this.uploadToken = uploadToken;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
