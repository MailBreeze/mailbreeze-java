package com.mailbreeze.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Status of an attachment upload.
 */
public enum AttachmentStatus {
    PENDING("pending"),
    UPLOADED("uploaded"),
    EXPIRED("expired");

    private final String value;

    AttachmentStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AttachmentStatus fromValue(String value) {
        for (AttachmentStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown attachment status: " + value);
    }
}
