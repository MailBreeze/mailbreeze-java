package com.mailbreeze.resources;

import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.models.*;

/**
 * Resource for email verification.
 *
 * <p>Verify email addresses before sending to improve deliverability and reduce bounces.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * // Verify a single email
 * VerifyEmailResult result = mailbreeze.verification().verify("user@example.com");
 * if (result.isValid()) {
 *     // Safe to send
 * }
 *
 * // Verify emails in batch
 * BatchVerifyResult batch = mailbreeze.verification().batch(
 *     BatchVerifyParams.of(List.of("a@example.com", "b@example.com"))
 * );
 *
 * // Check batch status
 * BatchVerifyResult status = mailbreeze.verification().get(batch.getVerificationId());
 *
 * // Get verification statistics
 * VerificationStats stats = mailbreeze.verification().stats();
 * }</pre>
 */
public class Verification extends BaseResource {

    /**
     * Creates a new Verification resource.
     *
     * @param httpClient the HTTP client for making requests
     */
    public Verification(MailBreezeHttpClient httpClient) {
        super(httpClient, "/email-verification");
    }

    /**
     * Verifies a single email address.
     *
     * @param email the email address to verify
     * @return the verification result
     */
    public VerifyEmailResult verify(String email) {
        record VerifyRequest(String email) {}
        return post("/single", new VerifyRequest(email), VerifyEmailResult.class, null);
    }

    /**
     * Starts a batch email verification.
     *
     * <p>For large lists, use batch verification. Results can be retrieved
     * using {@link #get(String)} once processing is complete.</p>
     *
     * @param params the batch parameters with list of emails
     * @return the batch verification result
     */
    public BatchVerifyResult batch(BatchVerifyParams params) {
        return post("/batch", params, BatchVerifyResult.class, null);
    }

    /**
     * Gets the status and results of a batch verification.
     *
     * @param verificationId the batch verification ID
     * @return the verification status and results
     */
    public BatchVerifyResult get(String verificationId) {
        return get("/" + verificationId + "?includeResults=true", null, BatchVerifyResult.class);
    }

    /**
     * Gets email verification statistics.
     *
     * @return the verification statistics
     */
    public VerificationStats stats() {
        return get("/stats", null, VerificationStats.class);
    }
}
