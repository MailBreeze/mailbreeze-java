package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mailbreeze.models.enums.VerificationResult;

/** Result of single email verification. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyEmailResult {

  private String email;

  @JsonProperty("is_valid")
  private boolean isValid;

  private VerificationResult result;
  private String reason;
  private boolean cached;

  @JsonProperty("risk_score")
  private Integer riskScore;

  private VerificationDetails details;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean isValid() {
    return isValid;
  }

  public void setValid(boolean valid) {
    isValid = valid;
  }

  public VerificationResult getResult() {
    return result;
  }

  public void setResult(VerificationResult result) {
    this.result = result;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public boolean isCached() {
    return cached;
  }

  public void setCached(boolean cached) {
    this.cached = cached;
  }

  public Integer getRiskScore() {
    return riskScore;
  }

  public void setRiskScore(Integer riskScore) {
    this.riskScore = riskScore;
  }

  public VerificationDetails getDetails() {
    return details;
  }

  public void setDetails(VerificationDetails details) {
    this.details = details;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class VerificationDetails {
    @JsonProperty("is_free_provider")
    private Boolean isFreeProvider;

    @JsonProperty("is_disposable")
    private Boolean isDisposable;

    @JsonProperty("is_role_account")
    private Boolean isRoleAccount;

    @JsonProperty("has_mx_records")
    private Boolean hasMxRecords;

    @JsonProperty("is_spam_trap")
    private Boolean isSpamTrap;

    public Boolean getFreeProvider() {
      return isFreeProvider;
    }

    public void setFreeProvider(Boolean freeProvider) {
      isFreeProvider = freeProvider;
    }

    public Boolean getDisposable() {
      return isDisposable;
    }

    public void setDisposable(Boolean disposable) {
      isDisposable = disposable;
    }

    public Boolean getRoleAccount() {
      return isRoleAccount;
    }

    public void setRoleAccount(Boolean roleAccount) {
      isRoleAccount = roleAccount;
    }

    public Boolean getHasMxRecords() {
      return hasMxRecords;
    }

    public void setHasMxRecords(Boolean hasMxRecords) {
      this.hasMxRecords = hasMxRecords;
    }

    public Boolean getSpamTrap() {
      return isSpamTrap;
    }

    public void setSpamTrap(Boolean spamTrap) {
      isSpamTrap = spamTrap;
    }
  }
}
