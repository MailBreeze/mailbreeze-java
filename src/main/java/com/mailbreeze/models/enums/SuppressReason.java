package com.mailbreeze.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Reason for suppressing a contact.
 */
public enum SuppressReason {
    MANUAL("manual"),
    UNSUBSCRIBED("unsubscribed"),
    BOUNCED("bounced"),
    COMPLAINED("complained"),
    SPAM_TRAP("spam_trap");

    private final String value;

    SuppressReason(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static SuppressReason fromValue(String value) {
        for (SuppressReason reason : values()) {
            if (reason.value.equals(value)) {
                return reason;
            }
        }
        throw new IllegalArgumentException("Unknown suppress reason: " + value);
    }
}
