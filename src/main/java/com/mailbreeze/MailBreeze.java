package com.mailbreeze;

import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.resources.*;

import java.time.Duration;

/**
 * Main entry point for the MailBreeze Java SDK.
 *
 * <p>Use the builder to create a client instance:</p>
 * <pre>{@code
 * MailBreeze mailbreeze = MailBreeze.builder()
 *     .apiKey("sk_live_your_api_key")
 *     .build();
 *
 * // Send an email
 * SendEmailResult result = mailbreeze.emails().send(
 *     SendEmailParams.builder()
 *         .from("sender@example.com")
 *         .to("recipient@example.com")
 *         .subject("Hello!")
 *         .html("<p>Welcome to MailBreeze!</p>")
 *         .build()
 * );
 * }</pre>
 *
 * <p>The client is thread-safe and should be reused across your application.</p>
 */
public final class MailBreeze {

    private static final String DEFAULT_BASE_URL = "https://api.mailbreeze.com";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    private static final int DEFAULT_MAX_RETRIES = 3;

    private final MailBreezeHttpClient httpClient;
    private final Emails emails;
    private final Lists lists;
    private final Attachments attachments;
    private final Verification verification;
    private final Automations automations;

    private MailBreeze(Builder builder) {
        String baseUrl = builder.baseUrl != null ? builder.baseUrl : DEFAULT_BASE_URL;
        Duration timeout = builder.timeout != null ? builder.timeout : DEFAULT_TIMEOUT;
        int maxRetries = builder.maxRetries != null ? builder.maxRetries : DEFAULT_MAX_RETRIES;

        this.httpClient = new MailBreezeHttpClient(builder.apiKey, baseUrl, timeout, maxRetries);
        this.emails = new Emails(httpClient);
        this.lists = new Lists(httpClient);
        this.attachments = new Attachments(httpClient);
        this.verification = new Verification(httpClient);
        this.automations = new Automations(httpClient);
    }

    /**
     * Creates a new builder for MailBreeze client configuration.
     *
     * @return a new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the Emails resource for sending and managing emails.
     *
     * @return the Emails resource
     */
    public Emails emails() {
        return emails;
    }

    /**
     * Returns the Lists resource for managing contact lists.
     *
     * @return the Lists resource
     */
    public Lists lists() {
        return lists;
    }

    /**
     * Returns a Contacts resource for the specified list.
     *
     * <p>Contacts are list-scoped, so you need to specify which list to work with.</p>
     *
     * @param listId the contact list ID
     * @return a Contacts resource for the specified list
     */
    public Contacts contacts(String listId) {
        return new Contacts(httpClient, listId);
    }

    /**
     * Returns the Attachments resource for file uploads.
     *
     * @return the Attachments resource
     */
    public Attachments attachments() {
        return attachments;
    }

    /**
     * Returns the Verification resource for email verification.
     *
     * @return the Verification resource
     */
    public Verification verification() {
        return verification;
    }

    /**
     * Returns the Automations resource for managing automation enrollments.
     *
     * @return the Automations resource
     */
    public Automations automations() {
        return automations;
    }

    /**
     * Builder for MailBreeze client configuration.
     */
    public static final class Builder {

        private String apiKey;
        private String baseUrl;
        private Duration timeout;
        private Integer maxRetries;

        private Builder() {}

        /**
         * Sets the API key for authentication.
         *
         * @param apiKey your MailBreeze API key (required)
         * @return this builder
         */
        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Sets the base URL for the API.
         *
         * @param baseUrl the base URL (default: https://api.mailbreeze.com)
         * @return this builder
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        /**
         * Sets the request timeout duration.
         *
         * @param timeout the timeout duration (default: 30 seconds)
         * @return this builder
         */
        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        /**
         * Sets the maximum number of retries for failed requests.
         *
         * @param maxRetries the max retries (default: 3)
         * @return this builder
         */
        public Builder maxRetries(int maxRetries) {
            this.maxRetries = maxRetries;
            return this;
        }

        /**
         * Builds the MailBreeze client.
         *
         * @return a new MailBreeze client
         * @throws IllegalArgumentException if API key is not set or is blank
         */
        public MailBreeze build() {
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalArgumentException("API key is required and cannot be blank");
            }
            return new MailBreeze(this);
        }
    }
}
