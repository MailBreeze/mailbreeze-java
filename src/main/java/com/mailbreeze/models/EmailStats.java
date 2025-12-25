package com.mailbreeze.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Email sending statistics.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailStats {

    private int sent;
    private int delivered;
    private int bounced;
    private int complained;
    private int opened;
    private int clicked;
    private int unsubscribed;

    public int getSent() {
        return sent;
    }

    public void setSent(int sent) {
        this.sent = sent;
    }

    public int getDelivered() {
        return delivered;
    }

    public void setDelivered(int delivered) {
        this.delivered = delivered;
    }

    public int getBounced() {
        return bounced;
    }

    public void setBounced(int bounced) {
        this.bounced = bounced;
    }

    public int getComplained() {
        return complained;
    }

    public void setComplained(int complained) {
        this.complained = complained;
    }

    public int getOpened() {
        return opened;
    }

    public void setOpened(int opened) {
        this.opened = opened;
    }

    public int getClicked() {
        return clicked;
    }

    public void setClicked(int clicked) {
        this.clicked = clicked;
    }

    public int getUnsubscribed() {
        return unsubscribed;
    }

    public void setUnsubscribed(int unsubscribed) {
        this.unsubscribed = unsubscribed;
    }
}
