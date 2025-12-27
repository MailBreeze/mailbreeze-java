package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mailbreeze.models.enums.ConsentType;
import com.mailbreeze.models.enums.ContactStatus;
import java.time.Instant;
import java.util.Map;

/** Represents a contact in a contact list. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contact {

  private String id;
  private String email;

  @JsonProperty("first_name")
  private String firstName;

  @JsonProperty("last_name")
  private String lastName;

  @JsonProperty("phone_number")
  private String phoneNumber;

  private ContactStatus status;
  private String source;

  @JsonProperty("custom_fields")
  private Map<String, Object> customFields;

  @JsonProperty("list_id")
  private String listId;

  @JsonProperty("created_at")
  private Instant createdAt;

  @JsonProperty("updated_at")
  private Instant updatedAt;

  @JsonProperty("subscribed_at")
  private Instant subscribedAt;

  @JsonProperty("unsubscribed_at")
  private Instant unsubscribedAt;

  @JsonProperty("consent_type")
  private ConsentType consentType;

  @JsonProperty("consent_source")
  private String consentSource;

  @JsonProperty("consent_timestamp")
  private Instant consentTimestamp;

  @JsonProperty("consent_ip_address")
  private String consentIpAddress;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public ContactStatus getStatus() {
    return status;
  }

  public void setStatus(ContactStatus status) {
    this.status = status;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public Map<String, Object> getCustomFields() {
    return customFields;
  }

  public void setCustomFields(Map<String, Object> customFields) {
    this.customFields = customFields;
  }

  public String getListId() {
    return listId;
  }

  public void setListId(String listId) {
    this.listId = listId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Instant getSubscribedAt() {
    return subscribedAt;
  }

  public void setSubscribedAt(Instant subscribedAt) {
    this.subscribedAt = subscribedAt;
  }

  public Instant getUnsubscribedAt() {
    return unsubscribedAt;
  }

  public void setUnsubscribedAt(Instant unsubscribedAt) {
    this.unsubscribedAt = unsubscribedAt;
  }

  public ConsentType getConsentType() {
    return consentType;
  }

  public void setConsentType(ConsentType consentType) {
    this.consentType = consentType;
  }

  public String getConsentSource() {
    return consentSource;
  }

  public void setConsentSource(String consentSource) {
    this.consentSource = consentSource;
  }

  public Instant getConsentTimestamp() {
    return consentTimestamp;
  }

  public void setConsentTimestamp(Instant consentTimestamp) {
    this.consentTimestamp = consentTimestamp;
  }

  public String getConsentIpAddress() {
    return consentIpAddress;
  }

  public void setConsentIpAddress(String consentIpAddress) {
    this.consentIpAddress = consentIpAddress;
  }
}
