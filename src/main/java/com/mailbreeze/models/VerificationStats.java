package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Email verification statistics. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerificationStats {

  private int totalVerified;
  private int totalValid;
  private int totalInvalid;
  private int totalUnknown;
  private int totalVerifications;
  private double validPercentage;

  public int getTotalVerified() {
    return totalVerified;
  }

  public void setTotalVerified(int totalVerified) {
    this.totalVerified = totalVerified;
  }

  public int getTotalValid() {
    return totalValid;
  }

  public void setTotalValid(int totalValid) {
    this.totalValid = totalValid;
  }

  public int getTotalInvalid() {
    return totalInvalid;
  }

  public void setTotalInvalid(int totalInvalid) {
    this.totalInvalid = totalInvalid;
  }

  public int getTotalUnknown() {
    return totalUnknown;
  }

  public void setTotalUnknown(int totalUnknown) {
    this.totalUnknown = totalUnknown;
  }

  public int getTotalVerifications() {
    return totalVerifications;
  }

  public void setTotalVerifications(int totalVerifications) {
    this.totalVerifications = totalVerifications;
  }

  public double getValidPercentage() {
    return validPercentage;
  }

  public void setValidPercentage(double validPercentage) {
    this.validPercentage = validPercentage;
  }
}
