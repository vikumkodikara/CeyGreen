package com.ceygreen.salesanalytics.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SalesSummaryDto {
    private String farmerId;
    private Long totalOrders;
    private BigDecimal totalRevenue;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdated;

    public SalesSummaryDto() {
    }

    public SalesSummaryDto(String farmerId, Long totalOrders, BigDecimal totalRevenue, LocalDateTime lastUpdated) {
        this.farmerId = farmerId;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.lastUpdated = lastUpdated;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public static class Builder {
        private String farmerId;
        private Long totalOrders;
        private BigDecimal totalRevenue;
        private LocalDateTime lastUpdated;

        public Builder farmerId(String farmerId) {
            this.farmerId = farmerId;
            return this;
        }

        public Builder totalOrders(Long totalOrders) {
            this.totalOrders = totalOrders;
            return this;
        }

        public Builder totalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
            return this;
        }

        public Builder lastUpdated(LocalDateTime lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public SalesSummaryDto build() {
            return new SalesSummaryDto(farmerId, totalOrders, totalRevenue, lastUpdated);
        }
    }
}
