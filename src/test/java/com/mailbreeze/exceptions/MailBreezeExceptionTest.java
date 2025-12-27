package com.mailbreeze.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MailBreeze Exception Hierarchy")
class MailBreezeExceptionTest {

  @Nested
  @DisplayName("MailBreezeException (Base)")
  class BaseExceptionTests {

    @Test
    @DisplayName("should contain all required fields")
    void shouldContainAllRequiredFields() {
      var exception =
          new MailBreezeException(
              400,
              "VALIDATION_ERROR",
              "Invalid email format",
              "req_123",
              Map.of("email", "must be valid"));

      assertThat(exception.getStatusCode()).isEqualTo(400);
      assertThat(exception.getCode()).isEqualTo("VALIDATION_ERROR");
      assertThat(exception.getMessage()).isEqualTo("Invalid email format");
      assertThat(exception.getRequestId()).isEqualTo("req_123");
      assertThat(exception.getDetails()).containsEntry("email", "must be valid");
    }

    @Test
    @DisplayName("should work with minimal fields")
    void shouldWorkWithMinimalFields() {
      var exception = new MailBreezeException(500, "SERVER_ERROR", "Internal error");

      assertThat(exception.getStatusCode()).isEqualTo(500);
      assertThat(exception.getCode()).isEqualTo("SERVER_ERROR");
      assertThat(exception.getMessage()).isEqualTo("Internal error");
      assertThat(exception.getRequestId()).isNull();
      assertThat(exception.getDetails()).isEmpty();
    }

    @Test
    @DisplayName("should have proper error message format")
    void shouldHaveProperErrorMessageFormat() {
      var exception =
          new MailBreezeException(400, "VALIDATION_ERROR", "Invalid email format", "req_123", null);

      String message = exception.toString();
      assertThat(message).contains("VALIDATION_ERROR");
      assertThat(message).contains("400");
      assertThat(message).contains("req_123");
    }

    @Test
    @DisplayName("should not leak sensitive data in toString")
    void shouldNotLeakSensitiveDataInToString() {
      var exception =
          new MailBreezeException(
              401, "AUTHENTICATION_ERROR", "Invalid API key: sk_live_secret123", null, null);

      // The message itself may contain sensitive data, that's the caller's responsibility
      // But we ensure toString format is safe
      assertThat(exception.toString()).doesNotContain("sk_live_secret123");
    }
  }

  @Nested
  @DisplayName("AuthenticationException")
  class AuthenticationExceptionTests {

    @Test
    @DisplayName("should have status code 401")
    void shouldHaveStatusCode401() {
      var exception = new AuthenticationException("Invalid API key", "req_456");

      assertThat(exception.getStatusCode()).isEqualTo(401);
      assertThat(exception.getCode()).isEqualTo("AUTHENTICATION_ERROR");
      assertThat(exception.getMessage()).isEqualTo("Invalid API key");
      assertThat(exception.getRequestId()).isEqualTo("req_456");
    }

    @Test
    @DisplayName("should be instance of MailBreezeException")
    void shouldBeInstanceOfMailBreezeException() {
      var exception = new AuthenticationException("Invalid API key", null);
      assertThat(exception).isInstanceOf(MailBreezeException.class);
    }
  }

  @Nested
  @DisplayName("ValidationException")
  class ValidationExceptionTests {

    @Test
    @DisplayName("should have status code 400")
    void shouldHaveStatusCode400() {
      Map<String, Object> details = Map.of("email", "must be valid", "subject", "is required");
      var exception = new ValidationException("Validation failed", "req_789", details);

      assertThat(exception.getStatusCode()).isEqualTo(400);
      assertThat(exception.getCode()).isEqualTo("VALIDATION_ERROR");
      assertThat(exception.getDetails()).isEqualTo(details);
    }

    @Test
    @DisplayName("should allow empty details")
    void shouldAllowEmptyDetails() {
      var exception = new ValidationException("Validation failed", null, null);
      assertThat(exception.getDetails()).isEmpty();
    }
  }

  @Nested
  @DisplayName("NotFoundException")
  class NotFoundExceptionTests {

    @Test
    @DisplayName("should have status code 404")
    void shouldHaveStatusCode404() {
      var exception = new NotFoundException("Email not found", "req_abc");

      assertThat(exception.getStatusCode()).isEqualTo(404);
      assertThat(exception.getCode()).isEqualTo("NOT_FOUND");
    }
  }

  @Nested
  @DisplayName("RateLimitException")
  class RateLimitExceptionTests {

    @Test
    @DisplayName("should have status code 429 and retryAfter")
    void shouldHaveStatusCode429AndRetryAfter() {
      var exception = new RateLimitException("Rate limit exceeded", "req_def", 60);

      assertThat(exception.getStatusCode()).isEqualTo(429);
      assertThat(exception.getCode()).isEqualTo("RATE_LIMIT_EXCEEDED");
      assertThat(exception.getRetryAfter()).isEqualTo(60);
    }

    @Test
    @DisplayName("should handle null retryAfter")
    void shouldHandleNullRetryAfter() {
      var exception = new RateLimitException("Rate limit exceeded", null, null);
      assertThat(exception.getRetryAfter()).isNull();
    }

    @Test
    @DisplayName("should be retryable")
    void shouldBeRetryable() {
      var exception = new RateLimitException("Rate limit exceeded", null, 30);
      assertThat(exception.isRetryable()).isTrue();
    }
  }

  @Nested
  @DisplayName("ServerException")
  class ServerExceptionTests {

    @Test
    @DisplayName("should have status code 500 or higher")
    void shouldHaveStatusCode500OrHigher() {
      var exception500 = new ServerException(500, "Internal server error", "req_ghi");
      var exception502 = new ServerException(502, "Bad gateway", "req_jkl");
      var exception503 = new ServerException(503, "Service unavailable", "req_mno");

      assertThat(exception500.getStatusCode()).isEqualTo(500);
      assertThat(exception502.getStatusCode()).isEqualTo(502);
      assertThat(exception503.getStatusCode()).isEqualTo(503);
      assertThat(exception500.getCode()).isEqualTo("SERVER_ERROR");
    }

    @Test
    @DisplayName("should be retryable")
    void shouldBeRetryable() {
      var exception = new ServerException(500, "Internal error", null);
      assertThat(exception.isRetryable()).isTrue();
    }
  }

  @Nested
  @DisplayName("Retryable Detection")
  class RetryableTests {

    @Test
    @DisplayName("RateLimitException should be retryable")
    void rateLimitShouldBeRetryable() {
      MailBreezeException exception = new RateLimitException("Rate limit", null, 30);
      assertThat(exception.isRetryable()).isTrue();
    }

    @Test
    @DisplayName("ServerException should be retryable")
    void serverExceptionShouldBeRetryable() {
      MailBreezeException exception = new ServerException(503, "Unavailable", null);
      assertThat(exception.isRetryable()).isTrue();
    }

    @Test
    @DisplayName("AuthenticationException should not be retryable")
    void authExceptionShouldNotBeRetryable() {
      MailBreezeException exception = new AuthenticationException("Bad key", null);
      assertThat(exception.isRetryable()).isFalse();
    }

    @Test
    @DisplayName("ValidationException should not be retryable")
    void validationExceptionShouldNotBeRetryable() {
      MailBreezeException exception = new ValidationException("Invalid", null, null);
      assertThat(exception.isRetryable()).isFalse();
    }

    @Test
    @DisplayName("NotFoundException should not be retryable")
    void notFoundExceptionShouldNotBeRetryable() {
      MailBreezeException exception = new NotFoundException("Not found", null);
      assertThat(exception.isRetryable()).isFalse();
    }
  }

  @Nested
  @DisplayName("Exception Factory")
  class ExceptionFactoryTests {

    @Test
    @DisplayName("should create correct exception type from status code 401")
    void shouldCreateAuthExceptionFrom401() {
      var exception = MailBreezeException.fromStatusCode(401, "Unauthorized", "req_1", null);
      assertThat(exception).isInstanceOf(AuthenticationException.class);
    }

    @Test
    @DisplayName("should create correct exception type from status code 400")
    void shouldCreateValidationExceptionFrom400() {
      var exception = MailBreezeException.fromStatusCode(400, "Bad request", "req_2", null);
      assertThat(exception).isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("should create correct exception type from status code 404")
    void shouldCreateNotFoundExceptionFrom404() {
      var exception = MailBreezeException.fromStatusCode(404, "Not found", "req_3", null);
      assertThat(exception).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("should create correct exception type from status code 429")
    void shouldCreateRateLimitExceptionFrom429() {
      var exception = MailBreezeException.fromStatusCode(429, "Too many requests", "req_4", null);
      assertThat(exception).isInstanceOf(RateLimitException.class);
    }

    @Test
    @DisplayName("should create correct exception type from status code 500+")
    void shouldCreateServerExceptionFrom5xx() {
      var exception500 = MailBreezeException.fromStatusCode(500, "Internal error", "req_5", null);
      var exception502 = MailBreezeException.fromStatusCode(502, "Bad gateway", "req_6", null);

      assertThat(exception500).isInstanceOf(ServerException.class);
      assertThat(exception502).isInstanceOf(ServerException.class);
    }

    @Test
    @DisplayName("should handle unknown status codes gracefully")
    void shouldHandleUnknownStatusCodes() {
      var exception = MailBreezeException.fromStatusCode(418, "I'm a teapot", "req_7", null);
      assertThat(exception).isInstanceOf(MailBreezeException.class);
      assertThat(exception.getStatusCode()).isEqualTo(418);
    }
  }
}
