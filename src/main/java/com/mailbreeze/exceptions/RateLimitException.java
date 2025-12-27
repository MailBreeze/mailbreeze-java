package com.mailbreeze.exceptions;

/**
 * Thrown when rate limit is exceeded (HTTP 429). Contains retry-after information if provided by
 * the server.
 */
public class RateLimitException extends MailBreezeException {

  private final Integer retryAfter;

  public RateLimitException(String message, String requestId, Integer retryAfter) {
    super(429, "RATE_LIMIT_EXCEEDED", message, requestId, null);
    this.retryAfter = retryAfter;
  }

  /** Returns the number of seconds to wait before retrying, or null if not specified. */
  public Integer getRetryAfter() {
    return retryAfter;
  }

  @Override
  public boolean isRetryable() {
    return true;
  }
}
