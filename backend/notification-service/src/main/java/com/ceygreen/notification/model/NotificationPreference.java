package com.ceygreen.notification.model;

import jakarta.persistence.*;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id", nullable = false, unique = true) private String userId;
    @Column(name = "email_enabled") private boolean emailEnabled = true;
    @Column(name = "push_enabled") private boolean pushEnabled = true;
    @Column(name = "order_alerts") private boolean orderAlerts = true;
    @Column(name = "stock_alerts") private boolean stockAlerts = true;
    @Column(name = "greenhouse_alerts") private boolean greenhouseAlerts = true;
    @Column(name = "forum_alerts") private boolean forumAlerts = true;

    public NotificationPreference() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public boolean isEmailEnabled() { return emailEnabled; }
    public void setEmailEnabled(boolean emailEnabled) { this.emailEnabled = emailEnabled; }
    public boolean isPushEnabled() { return pushEnabled; }
    public void setPushEnabled(boolean pushEnabled) { this.pushEnabled = pushEnabled; }
    public boolean isOrderAlerts() { return orderAlerts; }
    public void setOrderAlerts(boolean orderAlerts) { this.orderAlerts = orderAlerts; }
    public boolean isStockAlerts() { return stockAlerts; }
    public void setStockAlerts(boolean stockAlerts) { this.stockAlerts = stockAlerts; }
    public boolean isGreenhouseAlerts() { return greenhouseAlerts; }
    public void setGreenhouseAlerts(boolean greenhouseAlerts) { this.greenhouseAlerts = greenhouseAlerts; }
    public boolean isForumAlerts() { return forumAlerts; }
    public void setForumAlerts(boolean forumAlerts) { this.forumAlerts = forumAlerts; }
}
