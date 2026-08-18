package com.ceygreen.salesanalytics.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TreatmentEventDto {
    private String userId;
    private String farmerId;
    private String treatmentId;
    private String crop;
    private String treatmentApplied;
    private String dosage;
    private String status;

    public TreatmentEventDto() {
    }

    public TreatmentEventDto(String userId, String farmerId, String treatmentId, String crop, String treatmentApplied, String dosage, String status) {
        this.userId = userId;
        this.farmerId = farmerId;
        this.treatmentId = treatmentId;
        this.crop = crop;
        this.treatmentApplied = treatmentApplied;
        this.dosage = dosage;
        this.status = status;
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

    public String getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getCrop() {
        return crop;
    }

    public void setCrop(String crop) {
        this.crop = crop;
    }

    public String getTreatmentApplied() {
        return treatmentApplied;
    }

    public void setTreatmentApplied(String treatmentApplied) {
        this.treatmentApplied = treatmentApplied;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Builder {
        private String userId;
        private String farmerId;
        private String treatmentId;
        private String crop;
        private String treatmentApplied;
        private String dosage;
        private String status;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder farmerId(String farmerId) {
            this.farmerId = farmerId;
            return this;
        }

        public Builder treatmentId(String treatmentId) {
            this.treatmentId = treatmentId;
            return this;
        }

        public Builder crop(String crop) {
            this.crop = crop;
            return this;
        }

        public Builder treatmentApplied(String treatmentApplied) {
            this.treatmentApplied = treatmentApplied;
            return this;
        }

        public Builder dosage(String dosage) {
            this.dosage = dosage;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public TreatmentEventDto build() {
            return new TreatmentEventDto(userId, farmerId, treatmentId, crop, treatmentApplied, dosage, status);
        }
    }
}
