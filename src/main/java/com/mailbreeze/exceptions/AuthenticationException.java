package com.mailbreeze.exceptions;

/**
 * Thrown when API authentication fails (HTTP 401).
 * This typically means the API key is invalid, expired, or missing.
 */
public class AuthenticationException extends MailBreezeException {

    public AuthenticationException(String message, String requestId) {
        super(401, "AUTHENTICATION_ERROR", message, requestId, null);
    }

    @Override
    public boolean isRetryable() {
        return false;
    }
}
