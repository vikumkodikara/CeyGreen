package com.ceygreen.iot.model;

import java.time.Instant;

/**
 * A recommended action for the farmer after the rule engine evaluates a reading.
 * Stored under {@code /greenhouses/{id}/zones/{zoneId}/suggestions/{timestamp}}.
 */
public class Suggestion {

    private String id;
    private String greenhouseId;
    private String zoneId;
    private String zoneName;
    private String message;
    private String severity;
    private boolean resolved;
    private Instant createdAt;

    public Suggestion() {
    }

    public static Suggestion of(
            String greenhouseId,
            String zoneId,
            String zoneName,
            String message,
            String severity) {
        Suggestion suggestion = new Suggestion();
        suggestion.greenhouseId = greenhouseId;
        suggestion.zoneId = zoneId;
        suggestion.zoneName = zoneName;
        suggestion.message = message;
        suggestion.severity = severity;
        suggestion.resolved = false;
        suggestion.createdAt = Instant.now();
        suggestion.id = suggestion.createdAt.toString();
        return suggestion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGreenhouseId() {
        return greenhouseId;
    }

    public void setGreenhouseId(String greenhouseId) {
        this.greenhouseId = greenhouseId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
