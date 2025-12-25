package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Paginated response wrapper for list endpoints.
 *
 * @param <T> The type of items in the list
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaginatedResponse<T> {

    private List<T> items;
    private PaginationMeta pagination;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public PaginationMeta getPagination() {
        return pagination;
    }

    public void setPagination(PaginationMeta pagination) {
        this.pagination = pagination;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaginationMeta {
        private int page;
        private int limit;
        private int total;

        @JsonProperty("total_pages")
        private int totalPages;

        @JsonProperty("has_next")
        private boolean hasNext;

        @JsonProperty("has_prev")
        private boolean hasPrev;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public boolean isHasNext() {
            return hasNext;
        }

        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }

        public boolean isHasPrev() {
            return hasPrev;
        }

        public void setHasPrev(boolean hasPrev) {
            this.hasPrev = hasPrev;
        }
    }
}
