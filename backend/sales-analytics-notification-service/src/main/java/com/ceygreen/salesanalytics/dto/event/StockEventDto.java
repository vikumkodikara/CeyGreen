package com.ceygreen.salesanalytics.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StockEventDto {
    private String userId;
    private String farmerId;
    private String itemSku;
    private String itemName;
    private Double currentQuantity;
    private Double threshold;
    private String eventType;

    public StockEventDto() {
    }

    public StockEventDto(String userId, String farmerId, String itemSku, String itemName, Double currentQuantity, Double threshold, String eventType) {
        this.userId = userId;
        this.farmerId = farmerId;
        this.itemSku = itemSku;
        this.itemName = itemName;
        this.currentQuantity = currentQuantity;
        this.threshold = threshold;
        this.eventType = eventType;
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

    public String getItemSku() {
        return itemSku;
    }

    public void setItemSku(String itemSku) {
        this.itemSku = itemSku;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Double getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(Double currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public static class Builder {
        private String userId;
        private String farmerId;
        private String itemSku;
        private String itemName;
        private Double currentQuantity;
        private Double threshold;
        private String eventType;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder farmerId(String farmerId) {
            this.farmerId = farmerId;
            return this;
        }

        public Builder itemSku(String itemSku) {
            this.itemSku = itemSku;
            return this;
        }

        public Builder itemName(String itemName) {
            this.itemName = itemName;
            return this;
        }

        public Builder currentQuantity(Double currentQuantity) {
            this.currentQuantity = currentQuantity;
            return this;
        }

        public Builder threshold(Double threshold) {
            this.threshold = threshold;
            return this;
        }

        public Builder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public StockEventDto build() {
            return new StockEventDto(userId, farmerId, itemSku, itemName, currentQuantity, threshold, eventType);
        }
    }
}
