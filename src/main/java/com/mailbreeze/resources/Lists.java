package com.mailbreeze.resources;

import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.models.*;

import java.util.Map;

/**
 * Resource for managing contact lists.
 *
 * <p>Example usage:</p>
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
 * PaginatedResponse<ContactList> lists = mailbreeze.lists().list();
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
     * @return paginated list of contact lists
     */
    public PaginatedResponse<ContactList> list() {
        return list(null);
    }

    /**
     * Lists contact lists with filtering and pagination.
     *
     * @param params filter and pagination parameters
     * @return paginated list of contact lists
     */
    @SuppressWarnings("unchecked")
    public PaginatedResponse<ContactList> list(ListListsParams params) {
        Map<String, String> queryParams = params != null ? params.toQueryParams() : null;
        return (PaginatedResponse<ContactList>) get("", queryParams, ContactListPaginatedResponse.class);
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
        return patch("/" + listId, params, ContactList.class);
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
     * Concrete type for deserializing paginated contact list responses.
     */
    public static class ContactListPaginatedResponse extends PaginatedResponse<ContactList> {
    }
}
