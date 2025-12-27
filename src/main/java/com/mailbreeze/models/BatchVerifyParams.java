package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;

/** Parameters for batch email verification. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchVerifyParams {

  private List<String> emails;

  private BatchVerifyParams(List<String> emails) {
    this.emails = emails;
  }

  public static BatchVerifyParams of(List<String> emails) {
    if (emails == null || emails.isEmpty()) {
      throw new IllegalArgumentException("emails list is required and cannot be empty");
    }
    return new BatchVerifyParams(new ArrayList<>(emails));
  }

  public List<String> getEmails() {
    return emails;
  }
}
