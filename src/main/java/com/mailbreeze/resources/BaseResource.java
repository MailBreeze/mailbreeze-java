package com.mailbreeze.resources;

import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.http.RequestOptions;
import java.util.Map;

/**
 * Abstract base class for all API resources. Provides common HTTP methods with path building for
 * resource endpoints.
 *
 * <p>Each resource subclass defines a base path (e.g., "/emails", "/contacts") and all requests are
 * automatically prefixed with this path.
 *
 * <p>Example usage in subclass:
 *
 * <pre>{@code
 * public class Emails extends BaseResource {
 *     public Emails(MailBreezeHttpClient httpClient) {
 *         super(httpClient, "/emails");
 *     }
 *
 *     public Email get(String id) {
 *         return get("/" + id, null, Email.class);
 *     }
 * }
 * }</pre>
 */
public abstract class BaseResource {

  protected final MailBreezeHttpClient httpClient;
  private final String basePath;

  /**
   * Creates a new resource with the given HTTP client and base path.
   *
   * @param httpClient the HTTP client for making requests
   * @param basePath the base path for this resource (e.g., "/emails")
   */
  protected BaseResource(MailBreezeHttpClient httpClient, String basePath) {
    this.httpClient = httpClient;
    this.basePath = basePath;
  }

  /**
   * Builds the full path by prepending the base path.
   *
   * @param path the relative path (e.g., "/123" or "")
   * @return the full path (e.g., "/emails/123" or "/emails")
   */
  protected String buildPath(String path) {
    if (path == null || path.isEmpty()) {
      return basePath;
    }
    return basePath + path;
  }

  /**
   * Performs a GET request.
   *
   * @param path relative path to append to the base path
   * @param queryParams optional query parameters
   * @param responseType the expected response type
   * @param <T> the response type
   * @return the response object
   */
  protected <T> T get(String path, Map<String, String> queryParams, Class<T> responseType) {
    return httpClient.get(buildPath(path), queryParams, responseType);
  }

  /**
   * Performs a POST request.
   *
   * @param path relative path to append to the base path
   * @param body the request body
   * @param responseType the expected response type
   * @param options optional request options (e.g., idempotency key)
   * @param <T> the response type
   * @return the response object
   */
  protected <T> T post(String path, Object body, Class<T> responseType, RequestOptions options) {
    return httpClient.post(buildPath(path), body, responseType, options);
  }

  /**
   * Performs a PATCH request.
   *
   * @param path relative path to append to the base path
   * @param body the request body
   * @param responseType the expected response type
   * @param <T> the response type
   * @return the response object
   */
  protected <T> T patch(String path, Object body, Class<T> responseType) {
    return httpClient.patch(buildPath(path), body, responseType);
  }

  /**
   * Performs a PUT request.
   *
   * @param path relative path to append to the base path
   * @param body the request body
   * @param responseType the expected response type
   * @param <T> the response type
   * @return the response object
   */
  protected <T> T put(String path, Object body, Class<T> responseType) {
    return httpClient.put(buildPath(path), body, responseType);
  }

  /**
   * Performs a DELETE request.
   *
   * @param path relative path to append to the base path
   */
  protected void doDelete(String path) {
    httpClient.delete(buildPath(path));
  }
}
