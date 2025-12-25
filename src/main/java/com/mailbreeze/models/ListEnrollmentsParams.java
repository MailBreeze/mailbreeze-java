package com.mailbreeze.models;

import com.mailbreeze.models.enums.EnrollmentStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Parameters for listing automation enrollments.
 */
public class ListEnrollmentsParams {

    private String automationId;
    private EnrollmentStatus status;
    private Integer page;
    private Integer limit;

    private ListEnrollmentsParams(Builder builder) {
        this.automationId = builder.automationId;
        this.status = builder.status;
        this.page = builder.page;
        this.limit = builder.limit;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, String> toQueryParams() {
        Map<String, String> params = new HashMap<>();
        if (automationId != null) {
            params.put("automation_id", automationId);
        }
        if (status != null) {
            params.put("status", status.getValue());
        }
        if (page != null) {
            params.put("page", page.toString());
        }
        if (limit != null) {
            params.put("limit", limit.toString());
        }
        return params;
    }

    public static final class Builder {
        private String automationId;
        private EnrollmentStatus status;
        private Integer page;
        private Integer limit;

        private Builder() {}

        public Builder automationId(String automationId) {
            this.automationId = automationId;
            return this;
        }

        public Builder status(EnrollmentStatus status) {
            this.status = status;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public ListEnrollmentsParams build() {
            return new ListEnrollmentsParams(this);
        }
    }
}
