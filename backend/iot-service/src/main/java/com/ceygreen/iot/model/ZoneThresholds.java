package com.ceygreen.iot.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Per-zone limits used by the rule engine. Farmers can change these via PUT /api/iot/thresholds/{zoneId}.
 */
@IgnoreExtraProperties
public class ZoneThresholds {

    private double maxTemperature = 32.0;
    private double urgentMaxTemperature = 38.0;
    private double minTemperature = 24.0;
    private double minSoilMoisture = 35.0;
    private double urgentMinSoilMoisture = 20.0;
    private double maxSoilMoisture = 60.0;
    private double maxHumidity = 80.0;
    private double minHumidity = 60.0;
    private double minNitrogen = 10.0;
    private double minPhosphorus = 8.0;
    private double minPotassium = 8.0;

    public static ZoneThresholds defaults() {
        return new ZoneThresholds();
    }

    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public double getUrgentMaxTemperature() {
        return urgentMaxTemperature;
    }

    public void setUrgentMaxTemperature(double urgentMaxTemperature) {
        this.urgentMaxTemperature = urgentMaxTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public double getMinSoilMoisture() {
        return minSoilMoisture;
    }

    public void setMinSoilMoisture(double minSoilMoisture) {
        this.minSoilMoisture = minSoilMoisture;
    }

    public double getUrgentMinSoilMoisture() {
        return urgentMinSoilMoisture;
    }

    public void setUrgentMinSoilMoisture(double urgentMinSoilMoisture) {
        this.urgentMinSoilMoisture = urgentMinSoilMoisture;
    }

    public double getMaxSoilMoisture() {
        return maxSoilMoisture;
    }

    public void setMaxSoilMoisture(double maxSoilMoisture) {
        this.maxSoilMoisture = maxSoilMoisture;
    }

    public double getMaxHumidity() {
        return maxHumidity;
    }

    public void setMaxHumidity(double maxHumidity) {
        this.maxHumidity = maxHumidity;
    }

    public double getMinHumidity() {
        return minHumidity;
    }

    public void setMinHumidity(double minHumidity) {
        this.minHumidity = minHumidity;
    }

    public double getMinNitrogen() {
        return minNitrogen;
    }

    public void setMinNitrogen(double minNitrogen) {
        this.minNitrogen = minNitrogen;
    }

    public double getMinPhosphorus() {
        return minPhosphorus;
    }

    public void setMinPhosphorus(double minPhosphorus) {
        this.minPhosphorus = minPhosphorus;
    }

    public double getMinPotassium() {
        return minPotassium;
    }

    public void setMinPotassium(double minPotassium) {
        this.minPotassium = minPotassium;
    }
}
