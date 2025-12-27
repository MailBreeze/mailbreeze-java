package com.mailbreeze.models;

import java.util.HashMap;
import java.util.Map;

/** Parameters for listing contact lists. */
public class ListListsParams {

  private Integer page;
  private Integer limit;
  private String search;

  private ListListsParams(Builder builder) {
    this.page = builder.page;
    this.limit = builder.limit;
    this.search = builder.search;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Map<String, String> toQueryParams() {
    Map<String, String> params = new HashMap<>();
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
    private Integer page;
    private Integer limit;
    private String search;

    private Builder() {}

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

    public ListListsParams build() {
      return new ListListsParams(this);
    }
  }
}
