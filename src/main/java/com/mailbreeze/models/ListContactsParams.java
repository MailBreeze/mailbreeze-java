package com.mailbreeze.models;

import com.mailbreeze.models.enums.ContactStatus;
import java.util.HashMap;
import java.util.Map;

/** Parameters for listing contacts. */
public class ListContactsParams {

  private ContactStatus status;
  private Integer page;
  private Integer limit;
  private String search;

  private ListContactsParams(Builder builder) {
    this.status = builder.status;
    this.page = builder.page;
    this.limit = builder.limit;
    this.search = builder.search;
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
    if (search != null && !search.isBlank()) {
      params.put("search", search);
    }
    return params;
  }

  public static final class Builder {
    private ContactStatus status;
    private Integer page;
    private Integer limit;
    private String search;

    private Builder() {}

    public Builder status(ContactStatus status) {
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

    public Builder search(String search) {
      this.search = search;
      return this;
    }

    public ListContactsParams build() {
      return new ListContactsParams(this);
    }
  }
}
