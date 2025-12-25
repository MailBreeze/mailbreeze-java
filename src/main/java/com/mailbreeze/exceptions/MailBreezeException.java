package com.mailbreeze.exceptions;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Base exception for all MailBreeze API errors.
 * Contains structured error information from the API response.
 */
public class MailBreezeException extends RuntimeException {

    private static final Pattern API_KEY_PATTERN = Pattern.compile("sk_(live|test)_[a-zA-Z0-9]+");

    private final int statusCode;
    private final String code;
    private final String requestId;
    private final Map<String, Object> details;

    public MailBreezeException(int statusCode, String code, String message) {
        this(statusCode, code, message, null, null);
    }

    public MailBreezeException(int statusCode, String code, String message, String requestId, Map<String, Object> details) {
        super(sanitizeMessage(message));
        this.statusCode = statusCode;
        this.code = code;
        this.requestId = requestId;
        this.details = details != null ? Map.copyOf(details) : Collections.emptyMap();
    }

    /**
     * Creates the appropriate exception subclass based on HTTP status code.
     */
    public static MailBreezeException fromStatusCode(int statusCode, String message, String requestId, Map<String, Object> details) {
        return switch (statusCode) {
            case 400 -> new ValidationException(message, requestId, details);
            case 401 -> new AuthenticationException(message, requestId);
            case 404 -> new NotFoundException(message, requestId);
            case 429 -> new RateLimitException(message, requestId, null);
            default -> {
                if (statusCode >= 500) {
                    yield new ServerException(statusCode, message, requestId);
                }
                yield new MailBreezeException(statusCode, codeFromStatus(statusCode), message, requestId, details);
            }
        };
    }

    /**
     * Sanitizes message to remove potential API keys.
     */
    private static String sanitizeMessage(String message) {
        if (message == null) {
            return null;
        }
        return API_KEY_PATTERN.matcher(message).replaceAll("[REDACTED]");
    }

    private static String codeFromStatus(int statusCode) {
        return switch (statusCode) {
            case 400 -> "VALIDATION_ERROR";
            case 401 -> "AUTHENTICATION_ERROR";
            case 403 -> "FORBIDDEN";
            case 404 -> "NOT_FOUND";
            case 429 -> "RATE_LIMIT_EXCEEDED";
            default -> statusCode >= 500 ? "SERVER_ERROR" : "UNKNOWN_ERROR";
        };
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getCode() {
        return code;
    }

    public String getRequestId() {
        return requestId;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    /**
     * Returns true if this error is potentially transient and the request could be retried.
     * Only rate limit errors (429) and server errors (5xx) are considered retryable.
     */
    public boolean isRetryable() {
        return statusCode == 429 || statusCode >= 500;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MailBreezeException{");
        sb.append("statusCode=").append(statusCode);
        sb.append(", code='").append(code).append('\'');
        if (requestId != null) {
            sb.append(", requestId='").append(requestId).append('\'');
        }
        sb.append('}');
        return sb.toString();
    }
}
