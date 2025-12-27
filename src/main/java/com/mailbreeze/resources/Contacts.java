package com.mailbreeze.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mailbreeze.http.MailBreezeHttpClient;
import com.mailbreeze.models.*;
import com.mailbreeze.models.enums.SuppressReason;
import java.util.List;
import java.util.Map;

/**
 * Resource for managing contacts within a specific list.
 *
 * <p>Contacts are list-scoped, meaning you need to specify a list ID to work with contacts. Use
 * {@link com.mailbreeze.MailBreeze#contacts(String)} to get a Contacts instance for a specific
 * list.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * // Get contacts resource for a specific list
 * Contacts contacts = mailbreeze.contacts("list_123");
 *
 * // Create a contact
 * Contact contact = contacts.create(
 *     CreateContactParams.builder()
 *         .email("john@example.com")
 *         .firstName("John")
 *         .lastName("Doe")
 *         .build()
 * );
 *
 * // List contacts
 * ContactsResponse list = contacts.list(
 *     ListContactsParams.builder()
 *         .status(ContactStatus.ACTIVE)
 *         .limit(50)
 *         .build()
 * );
 * for (Contact c : list.getContacts()) {
 *     System.out.println(c.getEmail());
 * }
 *
 * // Update contact
 * Contact updated = contacts.update("contact_123",
 *     UpdateContactParams.builder()
 *         .firstName("Johnny")
 *         .build()
 * );
 *
 * // Suppress contact
 * contacts.suppress("contact_123", SuppressReason.MANUAL);
 *
 * // Delete contact
 * contacts.delete("contact_123");
 * }</pre>
 */
public class Contacts extends BaseResource {

  private final String listId;

  /**
   * Creates a new Contacts resource for a specific list.
   *
   * @param httpClient the HTTP client for making requests
   * @param listId the contact list ID
   */
  public Contacts(MailBreezeHttpClient httpClient, String listId) {
    super(httpClient, "/contact-lists");
    this.listId = listId;
  }

  /**
   * Builds the path including the list ID. Overrides the base path building to include:
   * /contact-lists/{listId}/contacts{path}
   */
  @Override
  protected String buildPath(String path) {
    return "/contact-lists/" + listId + "/contacts" + (path != null ? path : "");
  }

  /**
   * Creates a new contact in the list.
   *
   * @param params the contact parameters
   * @return the created contact
   */
  public Contact create(CreateContactParams params) {
    return post("", params, Contact.class, null);
  }

  /**
   * Lists all contacts in the list with default pagination.
   *
   * @return contacts response with list and pagination
   */
  public ContactsResponse list() {
    return list(null);
  }

  /**
   * Lists contacts with filtering and pagination.
   *
   * @param params filter and pagination parameters
   * @return contacts response with list and pagination
   */
  public ContactsResponse list(ListContactsParams params) {
    Map<String, String> queryParams = params != null ? params.toQueryParams() : null;
    return get("", queryParams, ContactsResponse.class);
  }

  /**
   * Gets a contact by ID.
   *
   * @param contactId the contact ID
   * @return the contact details
   */
  public Contact get(String contactId) {
    return get("/" + contactId, null, Contact.class);
  }

  /**
   * Updates a contact.
   *
   * @param contactId the contact ID
   * @param params the update parameters
   * @return the updated contact
   */
  public Contact update(String contactId, UpdateContactParams params) {
    return put("/" + contactId, params, Contact.class);
  }

  /**
   * Deletes a contact from the list.
   *
   * @param contactId the contact ID to delete
   */
  public void delete(String contactId) {
    doDelete("/" + contactId);
  }

  /**
   * Suppresses a contact.
   *
   * <p>Suppressed contacts will not receive any emails.
   *
   * @param contactId the contact ID to suppress
   * @param reason the reason for suppression
   */
  public void suppress(String contactId, SuppressReason reason) {
    SuppressRequest request = new SuppressRequest(reason.getValue());
    post("/" + contactId + "/suppress", request, Void.class, null);
  }

  /** Request body for suppress endpoint. */
  private record SuppressRequest(String reason) {}

  /**
   * Response wrapper for contacts list endpoint. Maps the API response format: {contacts: [...],
   * pagination: {...}}
   */
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ContactsResponse {
    @JsonProperty("contacts")
    private List<Contact> contacts;

    private PaginatedResponse.PaginationMeta pagination;

    public List<Contact> getContacts() {
      return contacts;
    }

    public void setContacts(List<Contact> contacts) {
      this.contacts = contacts;
    }

    public PaginatedResponse.PaginationMeta getPagination() {
      return pagination;
    }

    public void setPagination(PaginatedResponse.PaginationMeta pagination) {
      this.pagination = pagination;
    }
  }
}
