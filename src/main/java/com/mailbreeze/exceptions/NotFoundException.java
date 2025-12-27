package com.mailbreeze.exceptions;

/** Thrown when a requested resource is not found (HTTP 404). */
public class NotFoundException extends MailBreezeException {

  public NotFoundException(String message, String requestId) {
    super(404, "NOT_FOUND", message, requestId, null);
  }

  @Override
  public boolean isRetryable() {
    return false;
  }
}
