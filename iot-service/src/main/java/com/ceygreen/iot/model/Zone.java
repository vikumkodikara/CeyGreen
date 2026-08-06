package com.ceygreen.iot.model;

import java.util.Map;

/** A sub-area within a greenhouse, each with its own crop type and sensor thresholds. */
public class Zone {

    private String zoneId;
    private String zoneName;
    private String cropType;
    private Map<String, Double> thresholds;

    public Zone() {}

    public Zone(String zoneId, String zoneName, String cropType) {
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.cropType = cropType;
    }

    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }
    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }
    public Map<String, Double> getThresholds() { return thresholds; }
    public void setThresholds(Map<String, Double> thresholds) { this.thresholds = thresholds; }
}
