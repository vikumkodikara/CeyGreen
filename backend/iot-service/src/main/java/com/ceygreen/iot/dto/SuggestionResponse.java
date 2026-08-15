package com.ceygreen.iot.dto;

import com.ceygreen.iot.model.Suggestion;

/**
 * One suggestion shown on the web greenhouse dashboard.
 */
public class SuggestionResponse {

    private String zone;
    private String zoneId;
    private String message;
    private String severity;
    private boolean resolved;
    private String createdAt;

    public static SuggestionResponse from(Suggestion suggestion) {
        SuggestionResponse response = new SuggestionResponse();
        response.zone = suggestion.getZoneName();
        response.zoneId = suggestion.getZoneId();
        response.message = suggestion.getMessage();
        response.severity = suggestion.getSeverity();
        response.resolved = suggestion.isResolved();
        response.createdAt = suggestion.getCreatedAt();
        return response;
    }

    public String getZone() {
        return zone;
    }

    public String getZoneId() {
        return zoneId;
    }

    public String getMessage() {
        return message;
    }

    public String getSeverity() {
        return severity;
    }

    public boolean isResolved() {
        return resolved;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
