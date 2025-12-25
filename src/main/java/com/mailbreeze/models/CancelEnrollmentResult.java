package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mailbreeze.models.enums.EnrollmentStatus;

import java.time.Instant;

/**
 * Result of cancelling an enrollment.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelEnrollmentResult {

    @JsonProperty("enrollment_id")
    private String enrollmentId;

    private EnrollmentStatus status;

    @JsonProperty("cancelled_at")
    private Instant cancelledAt;

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}
