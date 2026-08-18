package com.ceygreen.salesanalytics.dto.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderEventDto {
    private String farmerId;
    private String orderId;
    private BigDecimal amount;
    private String product;
    private String customerName;
    private String status;

    public OrderEventDto() {
    }

    public OrderEventDto(String farmerId, String orderId, BigDecimal amount, String product, String customerName, String status) {
        this.farmerId = farmerId;
        this.orderId = orderId;
        this.amount = amount;
        this.product = product;
        this.customerName = customerName;
        this.status = status;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Builder {
        private String farmerId;
        private String orderId;
        private BigDecimal amount;
        private String product;
        private String customerName;
        private String status;

        public Builder farmerId(String farmerId) {
            this.farmerId = farmerId;
            return this;
        }

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder product(String product) {
            this.product = product;
            return this;
        }

        public Builder customerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public OrderEventDto build() {
            return new OrderEventDto(farmerId, orderId, amount, product, customerName, status);
        }
    }
}
