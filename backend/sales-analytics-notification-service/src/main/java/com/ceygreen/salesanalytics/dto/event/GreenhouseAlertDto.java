package com.ceygreen.salesanalytics.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GreenhouseAlertDto {
    private String userId;
    private String farmerId;
    private String greenhouseBay;
    private String alertType;
    private String severity;
    private String message;

    public GreenhouseAlertDto() {
    }

    public GreenhouseAlertDto(String userId, String farmerId, String greenhouseBay, String alertType, String severity, String message) {
        this.userId = userId;
        this.farmerId = farmerId;
        this.greenhouseBay = greenhouseBay;
        this.alertType = alertType;
        this.severity = severity;
        this.message = message;
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

    public String getGreenhouseBay() {
        return greenhouseBay;
    }

    public void setGreenhouseBay(String greenhouseBay) {
        this.greenhouseBay = greenhouseBay;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Builder {
        private String userId;
        private String farmerId;
        private String greenhouseBay;
        private String alertType;
        private String severity;
        private String message;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder farmerId(String farmerId) {
            this.farmerId = farmerId;
            return this;
        }

        public Builder greenhouseBay(String greenhouseBay) {
            this.greenhouseBay = greenhouseBay;
            return this;
        }

        public Builder alertType(String alertType) {
            this.alertType = alertType;
            return this;
        }

        public Builder severity(String severity) {
            this.severity = severity;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public GreenhouseAlertDto build() {
            return new GreenhouseAlertDto(userId, farmerId, greenhouseBay, alertType, severity, message);
        }
    }
}
