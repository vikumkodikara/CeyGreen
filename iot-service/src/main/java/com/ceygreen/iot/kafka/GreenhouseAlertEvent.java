package com.ceygreen.iot.kafka;

import java.time.Instant;

/**
 * Payload published to {@code greenhouse-alerts} for Student 6.
 */
public class GreenhouseAlertEvent {

    private String type = "greenhouse-alert";
    private String severity;
    private String message;
    private String greenhouseId;
    private String zoneId;
    private Double temperature;
    private Instant timestamp;

    public GreenhouseAlertEvent() {
    }

    public GreenhouseAlertEvent(
            String severity,
            String message,
            String greenhouseId,
            String zoneId,
            Double temperature,
            Instant timestamp) {
        this.severity = severity;
        this.message = message;
        this.greenhouseId = greenhouseId;
        this.zoneId = zoneId;
        this.temperature = temperature;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
