package com.ceygreen.salesanalytics.domain.entity;

import java.io.Serializable;
import java.util.Objects;

public class NotificationPreferenceId implements Serializable {
    private String userId;
    private String eventType;
    private String channel;

    public NotificationPreferenceId() {
    }

    public NotificationPreferenceId(String userId, String eventType, String channel) {
        this.userId = userId;
        this.eventType = eventType;
        this.channel = channel;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationPreferenceId that = (NotificationPreferenceId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(eventType, that.eventType) &&
               Objects.equals(channel, that.channel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, eventType, channel);
    }
}
