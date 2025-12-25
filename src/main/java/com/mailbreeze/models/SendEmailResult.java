package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mailbreeze.models.enums.EmailStatus;

/**
 * Result of sending an email.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendEmailResult {

    private String id;
    private EmailStatus status;

    @JsonProperty("message_id")
    private String messageId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public EmailStatus getStatus() {
        return status;
    }

    public void setStatus(EmailStatus status) {
        this.status = status;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
