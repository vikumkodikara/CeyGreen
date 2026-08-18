package com.ceygreen.iot.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * An ESP32 (or other edge device) attached to one zone.
 * Stored under {@code /greenhouses/{id}/zones/{zoneId}/devices/{deviceId}}.
 */
@IgnoreExtraProperties
public class Device {

    private String deviceId;
    private String deviceType;
    private String deviceCode;

    public Device() {
    }

    public Device(String deviceId, String deviceType, String deviceCode) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.deviceCode = deviceCode;
    }

    public static Device esp32(String deviceId, String deviceCode) {
        return new Device(deviceId, "ESP32", deviceCode);
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }
}
