package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Result of batch email verification request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchVerifyResult {

    @JsonProperty("verification_id")
    private String verificationId;

    @JsonProperty("total_emails")
    private int totalEmails;

    @JsonProperty("credits_deducted")
    private int creditsDeducted;

    private String status;

    private List<VerifyEmailResult> results;

    public String getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(String verificationId) {
        this.verificationId = verificationId;
    }

    public int getTotalEmails() {
        return totalEmails;
    }

    public void setTotalEmails(int totalEmails) {
        this.totalEmails = totalEmails;
    }

    public int getCreditsDeducted() {
        return creditsDeducted;
    }

    public void setCreditsDeducted(int creditsDeducted) {
        this.creditsDeducted = creditsDeducted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<VerifyEmailResult> getResults() {
        return results;
    }

    public void setResults(List<VerifyEmailResult> results) {
        this.results = results;
    }
}
