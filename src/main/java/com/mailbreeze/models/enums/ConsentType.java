package com.mailbreeze.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Type of consent obtained from the contact (NDPR compliance).
 */
public enum ConsentType {
    EXPLICIT("explicit"),
    IMPLICIT("implicit"),
    LEGITIMATE_INTEREST("legitimate_interest");

    private final String value;

    ConsentType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
