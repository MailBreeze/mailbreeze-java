package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Result of batch email verification request. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchVerifyResult {

  @JsonProperty("verificationId")
  private String verificationId;

  @JsonProperty("totalEmails")
  private int totalEmails;

  @JsonProperty("creditsDeducted")
  private int creditsDeducted;

  private String status;

  /** Categorized results with clean, dirty, and unknown email lists. */
  private BatchResults results;

  /** Analytics summary for batch verification. */
  private BatchAnalytics analytics;

  @JsonProperty("createdAt")
  private String createdAt;

  @JsonProperty("completedAt")
  private String completedAt;

  public String getVerificationId() {
    return verificationId;
  }

  public void setVerificationId(String verificationId) {
    this.verificationId = verificationId;
  }

  public int getTotalEmails() {
    return totalEmails;
  }

  public void setTotalEmails(int totalEmails) {
    this.totalEmails = totalEmails;
  }

  public int getCreditsDeducted() {
    return creditsDeducted;
  }

  public void setCreditsDeducted(int creditsDeducted) {
    this.creditsDeducted = creditsDeducted;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public BatchResults getResults() {
    return results;
  }

  public void setResults(BatchResults results) {
    this.results = results;
  }

  public BatchAnalytics getAnalytics() {
    return analytics;
  }

  public void setAnalytics(BatchAnalytics analytics) {
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

  /** Categorized batch results. */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class BatchResults {
    private List<String> clean;
    private List<String> dirty;
    private List<String> unknown;

    public List<String> getClean() {
      return clean;
    }

    public void setClean(List<String> clean) {
      this.clean = clean;
    }

    public List<String> getDirty() {
      return dirty;
    }

    public void setDirty(List<String> dirty) {
      this.dirty = dirty;
    }

    public List<String> getUnknown() {
      return unknown;
    }

    public void setUnknown(List<String> unknown) {
      this.unknown = unknown;
    }
  }

  /** Analytics for batch verification. */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class BatchAnalytics {
    @JsonProperty("cleanCount")
    private int cleanCount;

    @JsonProperty("dirtyCount")
    private int dirtyCount;

    @JsonProperty("unknownCount")
    private int unknownCount;

    @JsonProperty("cleanPercentage")
    private double cleanPercentage;

    public int getCleanCount() {
      return cleanCount;
    }

    public void setCleanCount(int cleanCount) {
      this.cleanCount = cleanCount;
    }

    public int getDirtyCount() {
      return dirtyCount;
    }

    public void setDirtyCount(int dirtyCount) {
      this.dirtyCount = dirtyCount;
    }

    public int getUnknownCount() {
      return unknownCount;
    }

    public void setUnknownCount(int unknownCount) {
      this.unknownCount = unknownCount;
    }

    public double getCleanPercentage() {
      return cleanPercentage;
    }

    public void setCleanPercentage(double cleanPercentage) {
      this.cleanPercentage = cleanPercentage;
    }
  }
}
