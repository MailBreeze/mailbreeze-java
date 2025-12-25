package com.mailbreeze.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mailbreeze.exceptions.*;
import okhttp3.*;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * HTTP client for MailBreeze API with retry logic and async support.
 * Thread-safe and reusable across requests.
 */
public class MailBreezeHttpClient {

    private static final String VERSION = "0.1.0";
    private static final MediaType JSON = MediaType.get("application/json");

    private final String apiKey;
    private final String baseUrl;
    private final int maxRetries;
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;

    public MailBreezeHttpClient(String apiKey, String baseUrl, Duration timeout, int maxRetries) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.maxRetries = maxRetries;

        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(timeout)
                .readTimeout(timeout)
                .writeTimeout(timeout)
                .build();

        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    // ==================== Sync Methods ====================

    public <T> T get(String path, Map<String, String> queryParams, Class<T> responseType) {
        return executeWithRetry(() -> doGet(path, queryParams, responseType));
    }

    public <T> T post(String path, Object body, Class<T> responseType, RequestOptions options) {
        return executeWithRetry(() -> doPost(path, body, responseType, options));
    }

    public <T> T patch(String path, Object body, Class<T> responseType) {
        return executeWithRetry(() -> doPatch(path, body, responseType));
    }

    public <T> T put(String path, Object body, Class<T> responseType) {
        return executeWithRetry(() -> doPut(path, body, responseType));
    }

    public void delete(String path) {
        executeWithRetry(() -> {
            doDelete(path);
            return null;
        });
    }

    // ==================== Async Methods ====================

    public <T> CompletableFuture<T> getAsync(String path, Map<String, String> queryParams, Class<T> responseType) {
        return executeWithRetryAsync(() -> doGetAsync(path, queryParams, responseType));
    }

    public <T> CompletableFuture<T> postAsync(String path, Object body, Class<T> responseType, RequestOptions options) {
        return executeWithRetryAsync(() -> doPostAsync(path, body, responseType, options));
    }

    public <T> CompletableFuture<T> patchAsync(String path, Object body, Class<T> responseType) {
        return executeWithRetryAsync(() -> doPatchAsync(path, body, responseType));
    }

    public CompletableFuture<Void> deleteAsync(String path) {
        return executeWithRetryAsync(() -> doDeleteAsync(path));
    }

    // ==================== Internal Request Methods ====================

    private <T> T doGet(String path, Map<String, String> queryParams, Class<T> responseType) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + path).newBuilder();
        if (queryParams != null) {
            queryParams.forEach(urlBuilder::addQueryParameter);
        }

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .headers(buildHeaders(null))
                .get()
                .build();

        return executeRequest(request, responseType);
    }

    private <T> T doPost(String path, Object body, Class<T> responseType, RequestOptions options) {
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .headers(buildHeaders(options))
                .post(createJsonBody(body))
                .build();

        return executeRequest(request, responseType);
    }

    private <T> T doPatch(String path, Object body, Class<T> responseType) {
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .headers(buildHeaders(null))
                .patch(createJsonBody(body))
                .build();

        return executeRequest(request, responseType);
    }

    private <T> T doPut(String path, Object body, Class<T> responseType) {
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .headers(buildHeaders(null))
                .put(createJsonBody(body))
                .build();

        return executeRequest(request, responseType);
    }

    private void doDelete(String path) {
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .headers(buildHeaders(null))
                .delete()
                .build();

        executeRequest(request, Void.class);
    }

    // ==================== Async Internal Methods ====================

    private <T> CompletableFuture<T> doGetAsync(String path, Map<String, String> queryParams, Class<T> responseType) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl + path).newBuilder();
        if (queryParams != null) {
            queryParams.forEach(urlBuilder::addQueryParameter);
        }

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .headers(buildHeaders(null))
                .get()
                .build();

        return executeRequestAsync(request, responseType);
    }

    private <T> CompletableFuture<T> doPostAsync(String path, Object body, Class<T> responseType, RequestOptions options) {
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .headers(buildHeaders(options))
                .post(createJsonBody(body))
                .build();

        return executeRequestAsync(request, responseType);
    }

    private <T> CompletableFuture<T> doPatchAsync(String path, Object body, Class<T> responseType) {
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .headers(buildHeaders(null))
                .patch(createJsonBody(body))
                .build();

        return executeRequestAsync(request, responseType);
    }

    private CompletableFuture<Void> doDeleteAsync(String path) {
        Request request = new Request.Builder()
                .url(baseUrl + path)
                .headers(buildHeaders(null))
                .delete()
                .build();

        return executeRequestAsync(request, Void.class);
    }

    // ==================== Request Execution ====================

    private <T> T executeRequest(Request request, Class<T> responseType) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            return handleResponse(response, responseType);
        } catch (IOException e) {
            throw new MailBreezeException(0, "NETWORK_ERROR", "Network error: " + e.getMessage());
        }
    }

    private <T> CompletableFuture<T> executeRequestAsync(Request request, Class<T> responseType) {
        CompletableFuture<T> future = new CompletableFuture<>();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(
                        new MailBreezeException(0, "NETWORK_ERROR", "Network error: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (response) {
                    T result = handleResponse(response, responseType);
                    future.complete(result);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
        });

        return future;
    }

    @SuppressWarnings("unchecked")
    private <T> T handleResponse(Response response, Class<T> responseType) {
        String requestId = response.header("X-Request-Id");
        Integer retryAfter = parseRetryAfter(response.header("Retry-After"));

        // Handle 204 No Content
        if (response.code() == 204) {
            return null;
        }

        String bodyString;
        try {
            ResponseBody body = response.body();
            bodyString = body != null ? body.string() : "";
        } catch (IOException e) {
            throw new MailBreezeException(response.code(), "RESPONSE_READ_ERROR",
                    "Failed to read response body", requestId, null);
        }

        // Parse response envelope
        ApiResponse apiResponse;
        try {
            apiResponse = objectMapper.readValue(bodyString, ApiResponse.class);
        } catch (JsonProcessingException e) {
            // If we can't parse the response, check HTTP status
            if (response.code() >= 400) {
                throw createExceptionFromStatus(response.code(), "HTTP error: " + response.code(),
                        requestId, retryAfter, null);
            }
            throw new MailBreezeException(response.code(), "PARSE_ERROR",
                    "Failed to parse response: " + e.getMessage(), requestId, null);
        }

        // Check for API error (success=false)
        if (!apiResponse.isSuccess() || apiResponse.getError() != null) {
            String message = apiResponse.getError() != null ? apiResponse.getError().getMessage() : "Unknown error";
            Map<String, Object> details = apiResponse.getError() != null ? apiResponse.getError().getDetails() : null;
            throw createExceptionFromStatus(response.code(), message, requestId, retryAfter, details);
        }

        // Check HTTP status code
        if (response.code() >= 400) {
            throw createExceptionFromStatus(response.code(), "HTTP error", requestId, retryAfter, null);
        }

        // Return data from envelope
        if (responseType == Void.class) {
            return null;
        }

        try {
            if (apiResponse.getData() == null) {
                return null;
            }
            // For JsonNode type, return directly without conversion
            if (responseType == JsonNode.class) {
                @SuppressWarnings("unchecked")
                T result = (T) apiResponse.getData();
                return result;
            }
            return objectMapper.treeToValue(apiResponse.getData(), responseType);
        } catch (JsonProcessingException e) {
            throw new MailBreezeException(response.code(), "PARSE_ERROR",
                    "Failed to parse response data: " + e.getMessage(), requestId, null);
        }
    }

    // ==================== Retry Logic ====================

    private <T> T executeWithRetry(RequestSupplier<T> supplier) {
        MailBreezeException lastException = null;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                return supplier.execute();
            } catch (MailBreezeException e) {
                lastException = e;
                if (!e.isRetryable() || attempt >= maxRetries) {
                    throw e;
                }
                sleep(calculateRetryDelay(attempt, e));
            }
        }

        throw lastException;
    }

    private <T> CompletableFuture<T> executeWithRetryAsync(AsyncRequestSupplier<T> supplier) {
        return executeWithRetryAsync(supplier, 0, null);
    }

    private <T> CompletableFuture<T> executeWithRetryAsync(AsyncRequestSupplier<T> supplier, int attempt, MailBreezeException lastException) {
        if (attempt > maxRetries) {
            return CompletableFuture.failedFuture(lastException);
        }

        return supplier.execute().exceptionally(ex -> {
            throw wrapException(ex);
        }).thenApply(result -> result).exceptionallyCompose(ex -> {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            if (cause instanceof MailBreezeException mbe) {
                if (!mbe.isRetryable() || attempt >= maxRetries) {
                    return CompletableFuture.failedFuture(mbe);
                }

                long delay = calculateRetryDelay(attempt, mbe);
                return CompletableFuture.supplyAsync(() -> null,
                                CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS))
                        .thenCompose(ignored -> executeWithRetryAsync(supplier, attempt + 1, mbe));
            }
            return CompletableFuture.failedFuture(cause);
        });
    }

    private RuntimeException wrapException(Throwable ex) {
        if (ex instanceof RuntimeException re) {
            return re;
        }
        return new RuntimeException(ex);
    }

    private long calculateRetryDelay(int attempt, MailBreezeException exception) {
        if (exception instanceof RateLimitException rle && rle.getRetryAfter() != null) {
            return rle.getRetryAfter() * 1000L;
        }
        // Exponential backoff: 1s, 2s, 4s, 8s...
        return (1L << attempt) * 1000L;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MailBreezeException(0, "INTERRUPTED", "Request interrupted");
        }
    }

    // ==================== Helpers ====================

    private Headers buildHeaders(RequestOptions options) {
        Headers.Builder builder = new Headers.Builder()
                .add("Content-Type", "application/json")
                .add("X-API-Key", apiKey)
                .add("User-Agent", "mailbreeze-java/" + VERSION);

        if (options != null && options.getIdempotencyKey() != null) {
            builder.add("X-Idempotency-Key", options.getIdempotencyKey());
        }

        return builder.build();
    }

    private RequestBody createJsonBody(Object body) {
        try {
            String json = objectMapper.writeValueAsString(body);
            return RequestBody.create(json, JSON);
        } catch (JsonProcessingException e) {
            throw new MailBreezeException(0, "SERIALIZATION_ERROR", "Failed to serialize request body");
        }
    }

    private MailBreezeException createExceptionFromStatus(int statusCode, String message,
                                                           String requestId, Integer retryAfter,
                                                           Map<String, Object> details) {
        return switch (statusCode) {
            case 400 -> new ValidationException(message, requestId, details);
            case 401 -> new AuthenticationException(message, requestId);
            case 404 -> new NotFoundException(message, requestId);
            case 429 -> new RateLimitException(message, requestId, retryAfter);
            default -> {
                if (statusCode >= 500) {
                    yield new ServerException(statusCode, message, requestId);
                }
                yield new MailBreezeException(statusCode, "UNKNOWN_ERROR", message, requestId, details);
            }
        };
    }

    private Integer parseRetryAfter(String header) {
        if (header == null) {
            return null;
        }
        try {
            return Integer.parseInt(header);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "MailBreezeHttpClient{" +
                "apiKey='[REDACTED]'" +
                ", baseUrl='" + baseUrl + '\'' +
                ", maxRetries=" + maxRetries +
                '}';
    }

    @FunctionalInterface
    private interface RequestSupplier<T> {
        T execute();
    }

    @FunctionalInterface
    private interface AsyncRequestSupplier<T> {
        CompletableFuture<T> execute();
    }
}
