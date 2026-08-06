package com.ceygreen.iot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Body for {@code PUT /api/iot/thresholds/{zoneId}}.
 * Farmer adjusts limits for their zone from the web app.
 */
public class UpdateThresholdRequest {

    @NotBlank
    private String greenhouseId;

    @NotNull
    @Positive
    private Double maxTemperature;

    @NotNull
    @Positive
    private Double urgentMaxTemperature;

    @NotNull
    @Positive
    private Double minSoilMoisture;

    @NotNull
    @Positive
    private Double maxHumidity;

    @NotNull
    @Positive
    private Double minNitrogen;

    @NotNull
    @Positive
    private Double minPhosphorus;

    @NotNull
    @Positive
    private Double minPotassium;

    public String getGreenhouseId() {
        return greenhouseId;
    }

    public void setGreenhouseId(String greenhouseId) {
        this.greenhouseId = greenhouseId;
    }

    public Double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(Double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public Double getUrgentMaxTemperature() {
        return urgentMaxTemperature;
    }

    public void setUrgentMaxTemperature(Double urgentMaxTemperature) {
        this.urgentMaxTemperature = urgentMaxTemperature;
    }

    public Double getMinSoilMoisture() {
        return minSoilMoisture;
    }

    public void setMinSoilMoisture(Double minSoilMoisture) {
        this.minSoilMoisture = minSoilMoisture;
    }

    public Double getMaxHumidity() {
        return maxHumidity;
    }

    public void setMaxHumidity(Double maxHumidity) {
        this.maxHumidity = maxHumidity;
    }

    public Double getMinNitrogen() {
        return minNitrogen;
    }

    public void setMinNitrogen(Double minNitrogen) {
        this.minNitrogen = minNitrogen;
    }

    public Double getMinPhosphorus() {
        return minPhosphorus;
    }

    public void setMinPhosphorus(Double minPhosphorus) {
        this.minPhosphorus = minPhosphorus;
    }

    public Double getMinPotassium() {
        return minPotassium;
    }

    public void setMinPotassium(Double minPotassium) {
        this.minPotassium = minPotassium;
    }
}
