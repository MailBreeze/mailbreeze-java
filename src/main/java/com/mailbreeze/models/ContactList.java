package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;

/** Represents a contact list in MailBreeze. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactList {

  private String id;
  private String name;
  private String description;

  @JsonProperty("custom_fields")
  private List<CustomFieldDefinition> customFields;

  @JsonProperty("contact_count")
  private Integer contactCount;

  @JsonProperty("created_at")
  private Instant createdAt;

  @JsonProperty("updated_at")
  private Instant updatedAt;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<CustomFieldDefinition> getCustomFields() {
    return customFields;
  }

  public void setCustomFields(List<CustomFieldDefinition> customFields) {
    this.customFields = customFields;
  }

  public Integer getContactCount() {
    return contactCount;
  }

  public void setContactCount(Integer contactCount) {
    this.contactCount = contactCount;
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
}
