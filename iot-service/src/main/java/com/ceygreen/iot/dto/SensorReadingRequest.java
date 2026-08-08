package com.ceygreen.iot.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Body for {@code POST /api/iot/readings}.
 * Sent by your ESP32 (or Postman while testing).
 */
public class SensorReadingRequest {

    @NotBlank
    private String greenhouseId;

    @NotBlank
    private String zoneId;

    @NotNull
    @Min(-40)
    @Max(80)
    private Double temperature;

    @NotNull
    @Min(0)
    @Max(100)
    private Double humidity;

    @NotNull
    @Min(0)
    @Max(100)
    private Double soilMoisture;

    @NotNull
    @Min(0)
    private Double n;

    @NotNull
    @Min(0)
    private Double p;

    @NotNull
    @Min(0)
    private Double k;

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

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getSoilMoisture() {
        return soilMoisture;
    }

    public void setSoilMoisture(Double soilMoisture) {
        this.soilMoisture = soilMoisture;
    }

    public Double getN() {
        return n;
    }

    public void setN(Double n) {
        this.n = n;
    }

    public Double getP() {
        return p;
    }

    public void setP(Double p) {
        this.p = p;
    }

    public Double getK() {
        return k;
    }

    public void setK(Double k) {
        this.k = k;
    }
}
