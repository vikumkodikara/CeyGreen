package com.ceygreen.iot.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * One zone inside {@link CreateGreenhouseRequest}.
 * Your physical setup uses a single zone + one ESP32.
 */
public class ZoneRequest {

    @NotBlank
    private String zoneId;

    @NotBlank
    private String zoneName;

    private String cropType = "Tomato";

    /** Optional ESP32 code; a default is generated if blank. */
    private String deviceCode;

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

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }
}
