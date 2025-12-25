package com.mailbreeze.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Result of email verification.
 */
public enum VerificationResult {
    VALID("valid"),
    INVALID("invalid"),
    RISKY("risky"),
    UNKNOWN("unknown");

    private final String value;

    VerificationResult(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static VerificationResult fromValue(String value) {
        for (VerificationResult result : values()) {
            if (result.value.equals(value)) {
                return result;
            }
        }
        throw new IllegalArgumentException("Unknown verification result: " + value);
    }
}
