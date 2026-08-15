package com.ceygreen.ecommerce.service;

import com.ceygreen.ecommerce.config.MarketplaceProperties;
import com.ceygreen.ecommerce.dto.StockEvent;
import com.ceygreen.ecommerce.entity.Product;
import com.ceygreen.ecommerce.kafka.StockEventPublisher;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class StockEventService {

    private final StockEventPublisher stockEventPublisher;
    private final MarketplaceProperties marketplaceProperties;

    public StockEventService(StockEventPublisher stockEventPublisher, MarketplaceProperties marketplaceProperties) {
        this.stockEventPublisher = stockEventPublisher;
        this.marketplaceProperties = marketplaceProperties;
    }

    public void evaluateQuantityChange(Product product, int previousQuantity, int currentQuantity) {
        int threshold = marketplaceProperties.getStock().getLowThreshold();
        int restockMinIncrease = marketplaceProperties.getStock().getRestockMinIncrease();

        if (currentQuantity > previousQuantity
                && currentQuantity - previousQuantity >= restockMinIncrease) {
            publish(product, previousQuantity, currentQuantity, threshold, StockEvent.TYPE_RESTOCKED);
        }

        if (previousQuantity > threshold && currentQuantity <= threshold) {
            publish(product, previousQuantity, currentQuantity, threshold, StockEvent.TYPE_LOW_STOCK);
        }
    }

    private void publish(Product product, int previousQuantity, int currentQuantity, int threshold, String eventType) {
        stockEventPublisher.publish(new StockEvent(
                UUID.randomUUID(),
                product.getId(),
                product.getFarmerId(),
                product.getCropName(),
                previousQuantity,
                currentQuantity,
                threshold,
                eventType,
                Instant.now()));
    }
}