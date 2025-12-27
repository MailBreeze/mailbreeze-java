package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Email sending statistics. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailStats {

  private int total;
  private int sent;
  private int failed;
  private int transactional;
  private int marketing;
  private double successRate;

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getSent() {
    return sent;
  }

  public void setSent(int sent) {
    this.sent = sent;
  }

  public int getFailed() {
    return failed;
  }

  public void setFailed(int failed) {
    this.failed = failed;
  }

  public int getTransactional() {
    return transactional;
  }

  public void setTransactional(int transactional) {
    this.transactional = transactional;
  }

  public int getMarketing() {
    return marketing;
  }

  public void setMarketing(int marketing) {
    this.marketing = marketing;
  }

  public double getSuccessRate() {
    return successRate;
  }

  public void setSuccessRate(double successRate) {
    this.successRate = successRate;
  }
}
