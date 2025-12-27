package com.mailbreeze.resources;

import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.models.*;
import java.util.List;
import java.util.Map;

/**
 * Resource for managing contact lists.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * // Create a list
 * ContactList list = mailbreeze.lists().create(
 *     CreateListParams.builder()
 *         .name("Newsletter Subscribers")
 *         .description("Weekly newsletter recipients")
 *         .build()
 * );
 *
 * // List all lists
 * ListsResponse lists = mailbreeze.lists().list();
 * for (ContactList l : lists.getData()) {
 *     System.out.println(l.getName());
 * }
 *
 * // Get list by ID
 * ContactList list = mailbreeze.lists().get("list_123");
 *
 * // Update list
 * ContactList updated = mailbreeze.lists().update("list_123",
 *     UpdateListParams.builder()
 *         .name("New Name")
 *         .build()
 * );
 *
 * // Delete list
 * mailbreeze.lists().delete("list_123");
 *
 * // Get list statistics
 * ListStats stats = mailbreeze.lists().stats("list_123");
 * }</pre>
 */
public class Lists extends BaseResource {

  /**
   * Creates a new Lists resource.
   *
   * @param httpClient the HTTP client for making requests
   */
  public Lists(MailBreezeHttpClient httpClient) {
    super(httpClient, "/contact-lists");
  }

  /**
   * Creates a new contact list.
   *
   * @param params the list parameters
   * @return the created contact list
   */
  public ContactList create(CreateListParams params) {
    return post("", params, ContactList.class, null);
  }

  /**
   * Lists all contact lists with default pagination.
   *
   * @return list response with contact lists
   */
  public ListsResponse list() {
    return list(null);
  }

  /**
   * Lists contact lists with filtering and pagination.
   *
   * @param params filter and pagination parameters
   * @return list response with contact lists
   */
  public ListsResponse list(ListListsParams params) {
    Map<String, String> queryParams = params != null ? params.toQueryParams() : null;
    // API returns data as a direct array
    ContactList[] lists = get("", queryParams, ContactList[].class);
    return new ListsResponse(lists != null ? List.of(lists) : List.of());
  }

  /**
   * Gets a contact list by ID.
   *
   * @param listId the list ID
   * @return the contact list details
   */
  public ContactList get(String listId) {
    return get("/" + listId, null, ContactList.class);
  }

  /**
   * Updates a contact list.
   *
   * @param listId the list ID
   * @param params the update parameters
   * @return the updated contact list
   */
  public ContactList update(String listId, UpdateListParams params) {
    return put("/" + listId, params, ContactList.class);
  }

  /**
   * Deletes a contact list.
   *
   * @param listId the list ID to delete
   */
  public void delete(String listId) {
    doDelete("/" + listId);
  }

  /**
   * Gets statistics for a contact list.
   *
   * @param listId the list ID
   * @return the list statistics
   */
  public ListStats stats(String listId) {
    return get("/" + listId + "/stats", null, ListStats.class);
  }

  /**
   * Response wrapper for lists endpoint. API returns data as a direct array, which is wrapped in
   * this response.
   */
  public static class ListsResponse {
    private final List<ContactList> data;

    /**
     * Creates a new ListsResponse with the given lists.
     *
     * @param data the list of contact lists
     */
    public ListsResponse(List<ContactList> data) {
      this.data = data;
    }

    /**
     * Gets the list of contact lists.
     *
     * @return the contact lists
     */
    public List<ContactList> getData() {
      return data;
    }
  }
}
