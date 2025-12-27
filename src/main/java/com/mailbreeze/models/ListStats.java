package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Statistics for a contact list. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListStats {

  @JsonProperty("total_contacts")
  private int totalContacts;

  @JsonProperty("active_contacts")
  private int activeContacts;

  @JsonProperty("unsubscribed_contacts")
  private int unsubscribedContacts;

  @JsonProperty("bounced_contacts")
  private int bouncedContacts;

  @JsonProperty("complained_contacts")
  private int complainedContacts;

  @JsonProperty("suppressed_contacts")
  private int suppressedContacts;

  public int getTotalContacts() {
    return totalContacts;
  }

  public void setTotalContacts(int totalContacts) {
    this.totalContacts = totalContacts;
  }

  public int getActiveContacts() {
    return activeContacts;
  }

  public void setActiveContacts(int activeContacts) {
    this.activeContacts = activeContacts;
  }

  public int getUnsubscribedContacts() {
    return unsubscribedContacts;
  }

  public void setUnsubscribedContacts(int unsubscribedContacts) {
    this.unsubscribedContacts = unsubscribedContacts;
  }

  public int getBouncedContacts() {
    return bouncedContacts;
  }

  public void setBouncedContacts(int bouncedContacts) {
    this.bouncedContacts = bouncedContacts;
  }

  public int getComplainedContacts() {
    return complainedContacts;
  }

  public void setComplainedContacts(int complainedContacts) {
    this.complainedContacts = complainedContacts;
  }

  public int getSuppressedContacts() {
    return suppressedContacts;
  }

  public void setSuppressedContacts(int suppressedContacts) {
    this.suppressedContacts = suppressedContacts;
  }
}
