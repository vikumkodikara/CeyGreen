package com.ceygreen.salesanalytics.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderLogDto {
    private Long id;
    private String farmerId;
    private String orderId;
    private BigDecimal amount;
    private String product;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime recordedAt;

    public OrderLogDto() {
    }

    public OrderLogDto(Long id, String farmerId, String orderId, BigDecimal amount, String product, LocalDateTime recordedAt) {
        this.id = id;
        this.farmerId = farmerId;
        this.orderId = orderId;
        this.amount = amount;
        this.product = product;
        this.recordedAt = recordedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public static class Builder {
        private Long id;
        private String farmerId;
        private String orderId;
        private BigDecimal amount;
        private String product;
        private LocalDateTime recordedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

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

        public Builder recordedAt(LocalDateTime recordedAt) {
            this.recordedAt = recordedAt;
            return this;
        }

        public OrderLogDto build() {
            return new OrderLogDto(id, farmerId, orderId, amount, product, recordedAt);
        }
    }
}
