package com.ceygreen.analytics.dto;

public record NotificationPreferenceRequest(
        Boolean emailEnabled,
        Boolean pushEnabled,
        Boolean orderAlerts,
        Boolean stockAlerts,
        Boolean greenhouseAlerts,
        Boolean forumAlerts) {}
