package com.ceygreen.salesanalytics.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class NotificationDto {
    private Long id;
    private String userId;
    private String sourceTopic;
    private String channel;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sentAt;
    private String status;

    public NotificationDto() {
    }

    public NotificationDto(Long id, String userId, String sourceTopic, String channel, String message, LocalDateTime sentAt, String status) {
        this.id = id;
        this.userId = userId;
        this.sourceTopic = sourceTopic;
        this.channel = channel;
        this.message = message;
        this.sentAt = sentAt;
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSourceTopic() {
        return sourceTopic;
    }

    public void setSourceTopic(String sourceTopic) {
        this.sourceTopic = sourceTopic;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Builder {
        private Long id;
        private String userId;
        private String sourceTopic;
        private String channel;
        private String message;
        private LocalDateTime sentAt;
        private String status;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder sourceTopic(String sourceTopic) {
            this.sourceTopic = sourceTopic;
            return this;
        }

        public Builder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder sentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public NotificationDto build() {
            return new NotificationDto(id, userId, sourceTopic, channel, message, sentAt, status);
        }
    }
}
