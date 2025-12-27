package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mailbreeze.models.enums.ConsentType;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/** Parameters for updating a contact. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateContactParams {

  private String email;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private Map<String, Object> customFields;
  private ConsentType consentType;
  private String consentSource;
  private Instant consentTimestamp;
  private String consentIpAddress;

  private UpdateContactParams(Builder builder) {
    this.email = builder.email;
    this.firstName = builder.firstName;
    this.lastName = builder.lastName;
    this.phoneNumber = builder.phoneNumber;
    this.customFields = builder.customFields;
    this.consentType = builder.consentType;
    this.consentSource = builder.consentSource;
    this.consentTimestamp = builder.consentTimestamp;
    this.consentIpAddress = builder.consentIpAddress;
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

  public ConsentType getConsentType() {
    return consentType;
  }

  public String getConsentSource() {
    return consentSource;
  }

  public Instant getConsentTimestamp() {
    return consentTimestamp;
  }

  public String getConsentIpAddress() {
    return consentIpAddress;
  }

  public static final class Builder {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Map<String, Object> customFields;
    private ConsentType consentType;
    private String consentSource;
    private Instant consentTimestamp;
    private String consentIpAddress;

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

    public Builder consentType(ConsentType consentType) {
      this.consentType = consentType;
      return this;
    }

    public Builder consentSource(String consentSource) {
      this.consentSource = consentSource;
      return this;
    }

    public Builder consentTimestamp(Instant consentTimestamp) {
      this.consentTimestamp = consentTimestamp;
      return this;
    }

    public Builder consentIpAddress(String consentIpAddress) {
      this.consentIpAddress = consentIpAddress;
      return this;
    }

    public UpdateContactParams build() {
      return new UpdateContactParams(this);
    }
  }
}
