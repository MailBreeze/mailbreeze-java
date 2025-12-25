package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Parameters for creating an attachment upload.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateAttachmentUploadParams {

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("file_size")
    private Long fileSize;

    private CreateAttachmentUploadParams(Builder builder) {
        this.fileName = builder.fileName;
        this.contentType = builder.contentType;
        this.fileSize = builder.fileSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public static final class Builder {
        private String fileName;
        private String contentType;
        private Long fileSize;

        private Builder() {}

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder fileSize(long fileSize) {
            this.fileSize = fileSize;
            return this;
        }

        public CreateAttachmentUploadParams build() {
            if (fileName == null || fileName.isBlank()) {
                throw new IllegalArgumentException("fileName is required");
            }
            if (contentType == null || contentType.isBlank()) {
                throw new IllegalArgumentException("contentType is required");
            }
            if (fileSize == null || fileSize <= 0) {
                throw new IllegalArgumentException("fileSize must be positive");
            }
            return new CreateAttachmentUploadParams(this);
        }
    }
}
