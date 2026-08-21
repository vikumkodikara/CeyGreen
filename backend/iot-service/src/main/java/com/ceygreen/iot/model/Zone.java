package com.ceygreen.iot.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * One area inside a greenhouse (e.g. Zone A). Each zone has an ESP32 and thresholds.
 * Stored under {@code /greenhouses/{id}/zones/{zoneId}}.
 * Readings and suggestions are sibling nodes, not fields on this class.
 */
@IgnoreExtraProperties
@JsonIgnoreProperties(ignoreUnknown = true)
public class Zone {

    private String zoneId;
    private String zoneName;
    private String cropType;
    private ZoneThresholds thresholds;
    private Map<String, Device> devices = new LinkedHashMap<>();

    public Zone() {
    }

    public Zone(String zoneId, String zoneName, String cropType) {
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.cropType = cropType;
        this.thresholds = ZoneThresholds.defaults();
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

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

    public ZoneThresholds getThresholds() {
        return thresholds;
    }

    public void setThresholds(ZoneThresholds thresholds) {
        this.thresholds = thresholds;
    }

    public Map<String, Device> getDevices() {
        return devices;
    }

    public void setDevices(Map<String, Device> devices) {
        this.devices = devices != null ? devices : new LinkedHashMap<>();
    }
}
