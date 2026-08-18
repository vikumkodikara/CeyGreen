package com.ceygreen.salesanalytics.dto;

import java.util.List;

public class NotificationPreferenceResponseDto {
    private String userId;
    private List<PreferenceEntryDto> preferences;

    public NotificationPreferenceResponseDto() {
    }

    public NotificationPreferenceResponseDto(String userId, List<PreferenceEntryDto> preferences) {
        this.userId = userId;
        this.preferences = preferences;
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

    public List<PreferenceEntryDto> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<PreferenceEntryDto> preferences) {
        this.preferences = preferences;
    }

    public static class PreferenceEntryDto {
        private String eventType;
        private String channel;
        private Boolean enabled;

        public PreferenceEntryDto() {
        }

        public PreferenceEntryDto(String eventType, String channel, Boolean enabled) {
            this.eventType = eventType;
            this.channel = channel;
            this.enabled = enabled;
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
            private Boolean enabled;

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

            public PreferenceEntryDto build() {
                return new PreferenceEntryDto(eventType, channel, enabled);
            }
        }
    }

    public static class Builder {
        private String userId;
        private List<PreferenceEntryDto> preferences;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder preferences(List<PreferenceEntryDto> preferences) {
            this.preferences = preferences;
            return this;
        }

        public NotificationPreferenceResponseDto build() {
            return new NotificationPreferenceResponseDto(userId, preferences);
        }
    }
}
