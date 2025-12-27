package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/** Definition of a custom field for contacts. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomFieldDefinition {

  private String key;
  private String label;
  private FieldType type;
  private Boolean required;

  @JsonProperty("default_value")
  private Object defaultValue;

  private List<String> options;

  public CustomFieldDefinition() {}

  private CustomFieldDefinition(Builder builder) {
    this.key = builder.key;
    this.label = builder.label;
    this.type = builder.type;
    this.required = builder.required;
    this.defaultValue = builder.defaultValue;
    this.options = builder.options;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public FieldType getType() {
    return type;
  }

  public void setType(FieldType type) {
    this.type = type;
  }

  public Boolean getRequired() {
    return required;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public Object getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(Object defaultValue) {
    this.defaultValue = defaultValue;
  }

  public List<String> getOptions() {
    return options;
  }

  public void setOptions(List<String> options) {
    this.options = options;
  }

  /** The type of custom field. */
  public enum FieldType {
    @JsonProperty("text")
    TEXT,
    @JsonProperty("number")
    NUMBER,
    @JsonProperty("date")
    DATE,
    @JsonProperty("boolean")
    BOOLEAN,
    @JsonProperty("select")
    SELECT
  }

  public static final class Builder {
    private String key;
    private String label;
    private FieldType type;
    private Boolean required;
    private Object defaultValue;
    private List<String> options;

    private Builder() {}

    public Builder key(String key) {
      this.key = key;
      return this;
    }

    public Builder label(String label) {
      this.label = label;
      return this;
    }

    public Builder type(FieldType type) {
      this.type = type;
      return this;
    }

    public Builder required(boolean required) {
      this.required = required;
      return this;
    }

    public Builder defaultValue(Object defaultValue) {
      this.defaultValue = defaultValue;
      return this;
    }

    public Builder options(List<String> options) {
      this.options = new ArrayList<>(options);
      return this;
    }

    public Builder options(String... options) {
      this.options = new ArrayList<>(List.of(options));
      return this;
    }

    public CustomFieldDefinition build() {
      if (key == null || key.isBlank()) {
        throw new IllegalArgumentException("key is required");
      }
      if (label == null || label.isBlank()) {
        throw new IllegalArgumentException("label is required");
      }
      if (type == null) {
        throw new IllegalArgumentException("type is required");
      }
      if (type == FieldType.SELECT && (options == null || options.isEmpty())) {
        throw new IllegalArgumentException("options are required for SELECT type");
      }
      return new CustomFieldDefinition(this);
    }
  }
}
