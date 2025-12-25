package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Parameters for creating a contact.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateContactParams {

    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("custom_fields")
    private Map<String, Object> customFields;

    private String source;

    private CreateContactParams(Builder builder) {
        this.email = builder.email;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.phoneNumber = builder.phoneNumber;
        this.customFields = builder.customFields;
        this.source = builder.source;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Map<String, Object> getCustomFields() {
        return customFields;
    }

    public String getSource() {
        return source;
    }

    public static final class Builder {
        private String email;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private Map<String, Object> customFields;
        private String source;

        private Builder() {}

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder customFields(Map<String, Object> customFields) {
            this.customFields = new HashMap<>(customFields);
            return this;
        }

        public Builder customField(String key, Object value) {
            if (this.customFields == null) {
                this.customFields = new HashMap<>();
            }
            this.customFields.put(key, value);
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public CreateContactParams build() {
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("email is required");
            }
            return new CreateContactParams(this);
        }
    }
}
