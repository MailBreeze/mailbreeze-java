package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Parameters for updating a contact list.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateListParams {

    private String name;
    private String description;

    private UpdateListParams(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static final class Builder {
        private String name;
        private String description;

        private Builder() {}

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public UpdateListParams build() {
            return new UpdateListParams(this);
        }
    }
}
