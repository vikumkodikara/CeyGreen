package com.ceygreen.salesanalytics.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DiagnosisEventDto {
    private String userId;
    private String farmerId;
    private String plantId;
    private String crop;
    private String diseaseDetected;
    private Double confidence;
    private String notes;

    public DiagnosisEventDto() {
    }

    public DiagnosisEventDto(String userId, String farmerId, String plantId, String crop, String diseaseDetected, Double confidence, String notes) {
        this.userId = userId;
        this.farmerId = farmerId;
        this.plantId = plantId;
        this.crop = crop;
        this.diseaseDetected = diseaseDetected;
        this.confidence = confidence;
        this.notes = notes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getDiseaseDetected() {
        return diseaseDetected;
    }

    public void setDiseaseDetected(String diseaseDetected) {
        this.diseaseDetected = diseaseDetected;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public static class Builder {
        private String userId;
        private String farmerId;
        private String plantId;
        private String crop;
        private String diseaseDetected;
        private Double confidence;
        private String notes;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder farmerId(String farmerId) {
            this.farmerId = farmerId;
            return this;
        }

        public Builder plantId(String plantId) {
            this.plantId = plantId;
            return this;
        }

        public Builder crop(String crop) {
            this.crop = crop;
            return this;
        }

        public Builder diseaseDetected(String diseaseDetected) {
            this.diseaseDetected = diseaseDetected;
            return this;
        }

        public Builder confidence(Double confidence) {
            this.confidence = confidence;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public DiagnosisEventDto build() {
            return new DiagnosisEventDto(userId, farmerId, plantId, crop, diseaseDetected, confidence, notes);
        }
    }
}
