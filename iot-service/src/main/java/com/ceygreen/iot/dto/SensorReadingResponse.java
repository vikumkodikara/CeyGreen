package com.ceygreen.iot.dto;

import com.ceygreen.iot.model.SensorReading;

import java.time.Instant;

/**
 * Confirmation returned after a reading is stored.
 */
public class SensorReadingResponse {

    private String greenhouseId;
    private String zoneId;
    private Instant timestamp;
    private double temperature;
    private double humidity;
    private double soilMoisture;
    private double n;
    private double p;
    private double k;
    private String status;

    public static SensorReadingResponse from(SensorReading reading) {
        SensorReadingResponse response = new SensorReadingResponse();
        response.greenhouseId = reading.getGreenhouseId();
        response.zoneId = reading.getZoneId();
        response.timestamp = reading.getTimestamp();
        response.temperature = reading.getTemperature();
        response.humidity = reading.getHumidity();
        response.soilMoisture = reading.getSoilMoisture();
        response.n = reading.getN();
        response.p = reading.getP();
        response.k = reading.getK();
        response.status = "SAVED";
        return response;
    }

    public String getGreenhouseId() {
        return greenhouseId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getSoilMoisture() {
        return soilMoisture;
    }

    public double getN() {
        return n;
    }

    public double getP() {
        return p;
    }

    public double getK() {
        return k;
    }

    public String getStatus() {
        return status;
    }
}
