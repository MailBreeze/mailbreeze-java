package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Wrapper for email stats response from API. The API returns {"stats": {...}} so we need this
 * wrapper to extract the nested object.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailStatsResponse {

  private EmailStats stats;

  public EmailStats getStats() {
    return stats;
  }

  public void setStats(EmailStats stats) {
    this.stats = stats;
  }
}
