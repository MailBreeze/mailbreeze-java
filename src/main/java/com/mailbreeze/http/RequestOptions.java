package com.mailbreeze.http;

import java.util.regex.Pattern;

/** Options for individual HTTP requests. */
public final class RequestOptions {

  private static final Pattern HEADER_INJECTION_PATTERN = Pattern.compile("[\\r\\n]");

  private final String idempotencyKey;

  private RequestOptions(Builder builder) {
    // Sanitize idempotency key to prevent header injection
    this.idempotencyKey = sanitizeHeaderValue(builder.idempotencyKey);
  }

  private static String sanitizeHeaderValue(String value) {
    if (value == null) {
      return null;
    }
    // Remove any CR/LF characters to prevent header injection
    return HEADER_INJECTION_PATTERN.matcher(value).replaceAll("");
  }

  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private String idempotencyKey;

    private Builder() {}

    public Builder idempotencyKey(String idempotencyKey) {
      this.idempotencyKey = idempotencyKey;
      return this;
    }

    public RequestOptions build() {
      return new RequestOptions(this);
    }
  }
}
