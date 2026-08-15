package com.ceygreen.notification.dto;

public record NotificationPreferenceRequest(
        Boolean emailEnabled,
        Boolean pushEnabled,
        Boolean orderAlerts,
        Boolean stockAlerts,
        Boolean greenhouseAlerts,
        Boolean forumAlerts) {}
