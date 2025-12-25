package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Email verification statistics.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerificationStats {

    @JsonProperty("total_verified")
    private int totalVerified;

    private int valid;
    private int invalid;
    private int risky;
    private int unknown;

    @JsonProperty("credits_used")
    private int creditsUsed;

    @JsonProperty("credits_remaining")
    private int creditsRemaining;

    public int getTotalVerified() {
        return totalVerified;
    }

    public void setTotalVerified(int totalVerified) {
        this.totalVerified = totalVerified;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    public int getInvalid() {
        return invalid;
    }

    public void setInvalid(int invalid) {
        this.invalid = invalid;
    }

    public int getRisky() {
        return risky;
    }

    public void setRisky(int risky) {
        this.risky = risky;
    }

    public int getUnknown() {
        return unknown;
    }

    public void setUnknown(int unknown) {
        this.unknown = unknown;
    }

    public int getCreditsUsed() {
        return creditsUsed;
    }

    public void setCreditsUsed(int creditsUsed) {
        this.creditsUsed = creditsUsed;
    }

    public int getCreditsRemaining() {
        return creditsRemaining;
    }

    public void setCreditsRemaining(int creditsRemaining) {
        this.creditsRemaining = creditsRemaining;
    }
}
