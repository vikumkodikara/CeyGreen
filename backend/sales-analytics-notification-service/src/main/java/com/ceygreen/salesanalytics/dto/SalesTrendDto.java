package com.ceygreen.salesanalytics.dto;

import java.math.BigDecimal;
import java.util.List;

public class SalesTrendDto {
    private String farmerId;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private Double averageOrderValue;
    private List<OrderLogDto> orderHistory;

    public SalesTrendDto() {
    }

    public SalesTrendDto(String farmerId, Long totalOrders, BigDecimal totalRevenue, Double averageOrderValue, List<OrderLogDto> orderHistory) {
        this.farmerId = farmerId;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.averageOrderValue = averageOrderValue;
        this.orderHistory = orderHistory;
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

    public Double getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(Double averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public List<OrderLogDto> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(List<OrderLogDto> orderHistory) {
        this.orderHistory = orderHistory;
    }

    public static class Builder {
        private String farmerId;
        private Long totalOrders;
        private BigDecimal totalRevenue;
        private Double averageOrderValue;
        private List<OrderLogDto> orderHistory;

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

        public Builder averageOrderValue(Double averageOrderValue) {
            this.averageOrderValue = averageOrderValue;
            return this;
        }

        public Builder orderHistory(List<OrderLogDto> orderHistory) {
            this.orderHistory = orderHistory;
            return this;
        }

        public SalesTrendDto build() {
            return new SalesTrendDto(farmerId, totalOrders, totalRevenue, averageOrderValue, orderHistory);
        }
    }
}
