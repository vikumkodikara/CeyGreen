package com.ceygreen.iot.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Per-zone limits for the hourly suggestion engine.
 */
@IgnoreExtraProperties
public class ZoneThresholds {

    private double maxTemperature = 30.0;
    private double urgentMaxTemperature = 38.0;
    private double minTemperature = 15.0;
    private double minSoilMoisture = 20.0;
    private double maxHumidity = 90.0;
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

    public double getMaxHumidity() {
        return maxHumidity;
    }

    public void setMaxHumidity(double maxHumidity) {
        this.maxHumidity = maxHumidity;
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
