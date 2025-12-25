package com.mailbreeze.exceptions;

/**
 * Thrown when a server error occurs (HTTP 5xx).
 * These errors are typically transient and can be retried.
 */
public class ServerException extends MailBreezeException {

    public ServerException(int statusCode, String message, String requestId) {
        super(statusCode, "SERVER_ERROR", message, requestId, null);
    }

    @Override
    public boolean isRetryable() {
        return true;
    }
}
