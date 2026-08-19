package com.ceygreen.salesanalytics.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "notification_preferences")
@IdClass(NotificationPreferenceId.class)
public class NotificationPreference {

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Id
    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Id
    @Column(name = "channel", nullable = false)
    private String channel;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    public NotificationPreference() {
    }

    public NotificationPreference(String userId, String eventType, String channel, Boolean enabled) {
        this.userId = userId;
        this.eventType = eventType;
        this.channel = channel;
        this.enabled = enabled != null ? enabled : true;
    }

    public static Builder builder() {
        return new Builder();
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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public static class Builder {
        private String userId;
        private String eventType;
        private String channel;
        private Boolean enabled = true;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public NotificationPreference build() {
            return new NotificationPreference(userId, eventType, channel, enabled);
        }
    }
}
