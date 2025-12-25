package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mailbreeze.models.enums.EnrollmentStatus;

import java.time.Instant;

/**
 * Result of enrolling a contact in an automation.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnrollResult {

    @JsonProperty("enrollment_id")
    private String enrollmentId;

    private EnrollmentStatus status;

    @JsonProperty("enrolled_at")
    private Instant enrolledAt;

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

    public Instant getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(Instant enrolledAt) {
        this.enrolledAt = enrolledAt;
    }
}
