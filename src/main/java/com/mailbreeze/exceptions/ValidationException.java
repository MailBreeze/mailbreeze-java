package com.mailbreeze.exceptions;

import java.util.Map;

/**
 * Thrown when request validation fails (HTTP 400).
 * Contains field-level validation errors in the details map.
 */
public class ValidationException extends MailBreezeException {

    public ValidationException(String message, String requestId, Map<String, Object> details) {
        super(400, "VALIDATION_ERROR", message, requestId, details);
    }

    @Override
    public boolean isRetryable() {
        return false;
    }
}
