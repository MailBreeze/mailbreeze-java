package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parameters for sending an email.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendEmailParams {

    private String from;
    private List<String> to;
    private String subject;
    private String html;
    private String text;

    @JsonProperty("template_id")
    private String templateId;

    private Map<String, Object> variables;

    @JsonProperty("attachment_ids")
    private List<String> attachmentIds;

    @JsonProperty("reply_to")
    private String replyTo;

    private List<String> cc;
    private List<String> bcc;
    private Map<String, String> headers;
    private List<String> tags;

    private SendEmailParams(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.subject = builder.subject;
        this.html = builder.html;
        this.text = builder.text;
        this.templateId = builder.templateId;
        this.variables = builder.variables;
        this.attachmentIds = builder.attachmentIds;
        this.replyTo = builder.replyTo;
        this.cc = builder.cc;
        this.bcc = builder.bcc;
        this.headers = builder.headers;
        this.tags = builder.tags;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getFrom() { return from; }
    public List<String> getTo() { return to; }
    public String getSubject() { return subject; }
    public String getHtml() { return html; }
    public String getText() { return text; }
    public String getTemplateId() { return templateId; }
    public Map<String, Object> getVariables() { return variables; }
    public List<String> getAttachmentIds() { return attachmentIds; }
    public String getReplyTo() { return replyTo; }
    public List<String> getCc() { return cc; }
    public List<String> getBcc() { return bcc; }
    public Map<String, String> getHeaders() { return headers; }
    public List<String> getTags() { return tags; }

    public static final class Builder {
        private String from;
        private List<String> to = new ArrayList<>();
        private String subject;
        private String html;
        private String text;
        private String templateId;
        private Map<String, Object> variables;
        private List<String> attachmentIds;
        private String replyTo;
        private List<String> cc;
        private List<String> bcc;
        private Map<String, String> headers;
        private List<String> tags;

        private Builder() {}

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(List<String> to) {
            this.to = new ArrayList<>(to);
            return this;
        }

        public Builder to(String... to) {
            this.to = new ArrayList<>(List.of(to));
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder html(String html) {
            this.html = html;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        public Builder variables(Map<String, Object> variables) {
            this.variables = new HashMap<>(variables);
            return this;
        }

        public Builder variable(String key, Object value) {
            if (this.variables == null) {
                this.variables = new HashMap<>();
            }
            this.variables.put(key, value);
            return this;
        }

        public Builder attachmentIds(List<String> attachmentIds) {
            this.attachmentIds = new ArrayList<>(attachmentIds);
            return this;
        }

        public Builder replyTo(String replyTo) {
            this.replyTo = replyTo;
            return this;
        }

        public Builder cc(List<String> cc) {
            this.cc = new ArrayList<>(cc);
            return this;
        }

        public Builder bcc(List<String> bcc) {
            this.bcc = new ArrayList<>(bcc);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = new HashMap<>(headers);
            return this;
        }

        public Builder header(String name, String value) {
            if (this.headers == null) {
                this.headers = new HashMap<>();
            }
            this.headers.put(name, value);
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = new ArrayList<>(tags);
            return this;
        }

        public SendEmailParams build() {
            if (from == null || from.isBlank()) {
                throw new IllegalArgumentException("from is required");
            }
            if (to == null || to.isEmpty()) {
                throw new IllegalArgumentException("to is required");
            }
            return new SendEmailParams(this);
        }
    }
}
