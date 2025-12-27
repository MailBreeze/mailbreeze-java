package com.mailbreeze.models.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VerificationStatus {
  VALID("valid"),
  INVALID("invalid"),
  RISKY("risky"),
  UNKNOWN("unknown");

  private final String value;

  VerificationStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
