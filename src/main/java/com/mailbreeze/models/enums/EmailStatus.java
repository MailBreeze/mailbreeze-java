package com.mailbreeze.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EmailStatus {
  PENDING("pending"),
  QUEUED("queued"),
  SENT("sent"),
  DELIVERED("delivered"),
  BOUNCED("bounced"),
  COMPLAINED("complained"),
  FAILED("failed");

  private final String value;

  EmailStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static EmailStatus fromValue(String value) {
    for (EmailStatus status : values()) {
      if (status.value.equals(value)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown email status: " + value);
  }
}
