package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Parameters for enrolling a contact in an automation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollParams {

    @JsonProperty("automation_id")
    private String automationId;

    @JsonProperty("contact_id")
    private String contactId;

    private Map<String, Object> variables;

    private EnrollParams(Builder builder) {
        this.automationId = builder.automationId;
        this.contactId = builder.contactId;
        this.variables = builder.variables;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAutomationId() {
        return automationId;
    }

    public String getContactId() {
        return contactId;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public static final class Builder {
        private String automationId;
        private String contactId;
        private Map<String, Object> variables;

        private Builder() {}

        public Builder automationId(String automationId) {
            this.automationId = automationId;
            return this;
        }

        public Builder contactId(String contactId) {
            this.contactId = contactId;
            return this;
        }

        public Builder variables(Map<String, Object> variables) {
            this.variables = new HashMap<>(variables);
            return this;
        }

        public Builder variable(String key, Object value) {
            if (this.variables == null) {
                this.variables = new HashMap<>();
            }
            this.variables.put(key, value);
            return this;
        }

        public EnrollParams build() {
            if (automationId == null || automationId.isBlank()) {
                throw new IllegalArgumentException("automationId is required");
            }
            if (contactId == null || contactId.isBlank()) {
                throw new IllegalArgumentException("contactId is required");
            }
            return new EnrollParams(this);
        }
    }
}
