package com.mailbreeze.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ContactStatus {
  ACTIVE("active"),
  UNSUBSCRIBED("unsubscribed"),
  BOUNCED("bounced"),
  COMPLAINED("complained"),
  SUPPRESSED("suppressed");

  private final String value;

  ContactStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static ContactStatus fromValue(String value) {
    for (ContactStatus status : values()) {
      if (status.value.equals(value)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown contact status: " + value);
  }
}
