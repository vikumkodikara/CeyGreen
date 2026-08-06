package com.ceygreen.iot.model;

import java.time.Instant;

/** A generated suggestion/recommendation for a zone based on sensor readings. */
public class Suggestion {

    private String zoneId;
    private String message;
    private String severity;
    private boolean resolved;
    private Instant timestamp;

    public Suggestion() {}

    public Suggestion(String zoneId, String message, String severity) {
        this.zoneId = zoneId;
        this.message = message;
        this.severity = severity;
        this.resolved = false;
        this.timestamp = Instant.now();
    }

    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
