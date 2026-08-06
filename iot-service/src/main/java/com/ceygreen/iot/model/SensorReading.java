package com.ceygreen.iot.model;

import java.time.Instant;

/** Hourly sensor reading from an ESP32 device in a zone. */
public class SensorReading {

    private String zoneId;
    private String greenhouseId;
    private double temperature;
    private double humidity;
    private double soilMoisture;
    private double nitrogen;
    private double phosphorus;
    private double potassium;
    private Instant timestamp;

    public SensorReading() {}

    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }
    public String getGreenhouseId() { return greenhouseId; }
    public void setGreenhouseId(String greenhouseId) { this.greenhouseId = greenhouseId; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity = humidity; }
    public double getSoilMoisture() { return soilMoisture; }
    public void setSoilMoisture(double soilMoisture) { this.soilMoisture = soilMoisture; }
    public double getNitrogen() { return nitrogen; }
    public void setNitrogen(double nitrogen) { this.nitrogen = nitrogen; }
    public double getPhosphorus() { return phosphorus; }
    public void setPhosphorus(double phosphorus) { this.phosphorus = phosphorus; }
    public double getPotassium() { return potassium; }
    public void setPotassium(double potassium) { this.potassium = potassium; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
