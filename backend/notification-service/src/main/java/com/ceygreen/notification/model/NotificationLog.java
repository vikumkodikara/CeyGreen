package com.ceygreen.notification.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "notification_log")
public class NotificationLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id", nullable = false) private String userId;
    @Column(nullable = false) private String type;
    @Column(nullable = false) private String message;
    @Column(name = "is_read", nullable = false) private boolean read = false;
    @Column(name = "created_at") private Instant createdAt = Instant.now();

    public NotificationLog() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
