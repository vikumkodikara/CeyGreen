package com.ceygreen.ecommerce.config;

import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ceygreen.marketplace")
public class MarketplaceProperties {

    private final Stock stock = new Stock();

    public Stock getStock() {
        return stock;
    }

    public static class Stock {

        @Min(0)
        private int lowThreshold = 10;

        @Min(1)
        private int restockMinIncrease = 5;

        public int getLowThreshold() {
            return lowThreshold;
        }

        public void setLowThreshold(int lowThreshold) {
            this.lowThreshold = lowThreshold;
        }

        public int getRestockMinIncrease() {
            return restockMinIncrease;
        }

        public void setRestockMinIncrease(int restockMinIncrease) {
            this.restockMinIncrease = restockMinIncrease;
        }
    }
}