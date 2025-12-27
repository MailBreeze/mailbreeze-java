package com.mailbreeze.models;

import com.mailbreeze.models.enums.EmailStatus;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/** Parameters for listing emails. */
public class ListEmailsParams {

  private EmailStatus status;
  private Integer page;
  private Integer limit;
  private Instant fromDate;
  private Instant toDate;

  private ListEmailsParams(Builder builder) {
    this.status = builder.status;
    this.page = builder.page;
    this.limit = builder.limit;
    this.fromDate = builder.fromDate;
    this.toDate = builder.toDate;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Map<String, String> toQueryParams() {
    Map<String, String> params = new HashMap<>();
    if (status != null) {
      params.put("status", status.getValue());
    }
    if (page != null) {
      params.put("page", page.toString());
    }
    if (limit != null) {
      params.put("limit", limit.toString());
    }
    if (fromDate != null) {
      params.put("from_date", fromDate.toString());
    }
    if (toDate != null) {
      params.put("to_date", toDate.toString());
    }
    return params;
  }

  public static final class Builder {
    private EmailStatus status;
    private Integer page;
    private Integer limit;
    private Instant fromDate;
    private Instant toDate;

    private Builder() {}

    public Builder status(EmailStatus status) {
      this.status = status;
      return this;
    }

    public Builder page(int page) {
      this.page = page;
      return this;
    }

    public Builder limit(int limit) {
      this.limit = limit;
      return this;
    }

    public Builder fromDate(Instant fromDate) {
      this.fromDate = fromDate;
      return this;
    }

    public Builder toDate(Instant toDate) {
      this.toDate = toDate;
      return this;
    }

    public ListEmailsParams build() {
      return new ListEmailsParams(this);
    }
  }
}
