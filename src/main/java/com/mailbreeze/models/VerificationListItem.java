package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Item in verification list response. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerificationListItem {

  private String id;

  @JsonProperty("type")
  private String verificationType;

  private String status;

  @JsonProperty("totalEmails")
  private int totalEmails;

  private int progress;

  private BatchVerifyResult.BatchAnalytics analytics;

  @JsonProperty("createdAt")
  private String createdAt;

  @JsonProperty("completedAt")
  private String completedAt;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getVerificationType() {
    return verificationType;
  }

  public void setVerificationType(String verificationType) {
    this.verificationType = verificationType;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public int getTotalEmails() {
    return totalEmails;
  }

  public void setTotalEmails(int totalEmails) {
    this.totalEmails = totalEmails;
  }

  public int getProgress() {
    return progress;
  }

  public void setProgress(int progress) {
    this.progress = progress;
  }

  public BatchVerifyResult.BatchAnalytics getAnalytics() {
    return analytics;
  }

  public void setAnalytics(BatchVerifyResult.BatchAnalytics analytics) {
    this.analytics = analytics;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getCompletedAt() {
    return completedAt;
  }

  public void setCompletedAt(String completedAt) {
    this.completedAt = completedAt;
  }
}
