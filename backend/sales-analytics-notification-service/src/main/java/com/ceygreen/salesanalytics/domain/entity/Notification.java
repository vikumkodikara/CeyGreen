package com.ceygreen.salesanalytics.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "source_topic", nullable = false)
    private String sourceTopic;

    @Column(name = "channel", nullable = false)
    private String channel;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "status", nullable = false)
    private String status;

    public Notification() {
    }

    public Notification(Long id, String userId, String sourceTopic, String channel, String message, LocalDateTime sentAt, String status) {
        this.id = id;
        this.userId = userId;
        this.sourceTopic = sourceTopic;
        this.channel = channel;
        this.message = message;
        this.sentAt = sentAt != null ? sentAt : LocalDateTime.now();
        this.status = status != null ? status : "DELIVERED";
    }

    @PrePersist
    protected void onCreate() {
        if (this.sentAt == null) {
            this.sentAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "DELIVERED";
        }
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
        private String status = "DELIVERED";

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

        public Notification build() {
            return new Notification(id, userId, sourceTopic, channel, message, sentAt, status);
        }
    }
}
