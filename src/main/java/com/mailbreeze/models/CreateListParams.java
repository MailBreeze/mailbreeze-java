package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/** Parameters for creating a contact list. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateListParams {

  private String name;
  private String description;

  @JsonProperty("custom_fields")
  private List<CustomFieldDefinition> customFields;

  private CreateListParams(Builder builder) {
    this.name = builder.name;
    this.description = builder.description;
    this.customFields = builder.customFields;
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

  public List<CustomFieldDefinition> getCustomFields() {
    return customFields;
  }

  public static final class Builder {
    private String name;
    private String description;
    private List<CustomFieldDefinition> customFields;

    private Builder() {}

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder customFields(List<CustomFieldDefinition> customFields) {
      this.customFields = new ArrayList<>(customFields);
      return this;
    }

    public Builder customField(CustomFieldDefinition customField) {
      if (this.customFields == null) {
        this.customFields = new ArrayList<>();
      }
      this.customFields.add(customField);
      return this;
    }

    public CreateListParams build() {
      if (name == null || name.isBlank()) {
        throw new IllegalArgumentException("name is required");
      }
      return new CreateListParams(this);
    }
  }
}
