package com.ceygreen.salesanalytics.dto;

import jakarta.validation.constraints.NotBlank;

public class NotificationPreferenceRequestDto {

    @NotBlank(message = "event_type is required")
    private String eventType;

    @NotBlank(message = "channel is required (e.g., EMAIL, SMS, IN_APP, PUSH)")
    private String channel;

    private Boolean enabled = true;

    public NotificationPreferenceRequestDto() {
    }

    public NotificationPreferenceRequestDto(String eventType, String channel, Boolean enabled) {
        this.eventType = eventType;
        this.channel = channel;
        this.enabled = enabled != null ? enabled : true;
    }

    public static Builder builder() {
        return new Builder();
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
        private String eventType;
        private String channel;
        private Boolean enabled = true;

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

        public NotificationPreferenceRequestDto build() {
            return new NotificationPreferenceRequestDto(eventType, channel, enabled);
        }
    }
}
