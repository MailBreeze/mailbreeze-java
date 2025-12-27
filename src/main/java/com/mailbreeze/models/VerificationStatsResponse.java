package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Wrapper for verification stats response from API. The API returns {"stats": {...}} so we need
 * this wrapper to extract the nested object.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerificationStatsResponse {

  private VerificationStats stats;

  public VerificationStats getStats() {
    return stats;
  }

  public void setStats(VerificationStats stats) {
    this.stats = stats;
  }
}
