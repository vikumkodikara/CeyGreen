package com.ceygreen.ecommerce.service;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.ceygreen.ecommerce.config.MarketplaceProperties;
import com.ceygreen.ecommerce.dto.StockEvent;
import com.ceygreen.ecommerce.entity.Product;
import com.ceygreen.ecommerce.kafka.StockEventPublisher;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockEventServiceTest {

    @Mock
    private StockEventPublisher stockEventPublisher;

    private StockEventService stockEventService;

    @BeforeEach
    void setUp() {
        MarketplaceProperties properties = new MarketplaceProperties();
        properties.getStock().setLowThreshold(10);
        properties.getStock().setRestockMinIncrease(5);
        stockEventService = new StockEventService(stockEventPublisher, properties);
    }

    @Test
    void publishesLowStockWhenCrossingThresholdDownward() {
        stockEventService.evaluateQuantityChange(sampleProduct(), 12, 8);

        verify(stockEventPublisher).publish(argThat(event ->
                StockEvent.TYPE_LOW_STOCK.equals(event.eventType())
                        && event.previousQuantity() == 12
                        && event.currentQuantity() == 8));
    }

    @Test
    void doesNotPublishLowStockWhenAlreadyBelowThreshold() {
        stockEventService.evaluateQuantityChange(sampleProduct(), 8, 5);

        verify(stockEventPublisher, never()).publish(argThat(event ->
                StockEvent.TYPE_LOW_STOCK.equals(event.eventType())));
    }

    @Test
    void publishesRestockedWhenQuantityIncreasesEnough() {
        stockEventService.evaluateQuantityChange(sampleProduct(), 3, 20);

        verify(stockEventPublisher).publish(argThat(event ->
                StockEvent.TYPE_RESTOCKED.equals(event.eventType())
                        && event.previousQuantity() == 3
                        && event.currentQuantity() == 20));
    }

    @Test
    void doesNotPublishRestockedWhenQuantityDecreases() {
        stockEventService.evaluateQuantityChange(sampleProduct(), 20, 3);

        verify(stockEventPublisher, never()).publish(argThat(event ->
                StockEvent.TYPE_RESTOCKED.equals(event.eventType())));
    }

    @Test
    void doesNotPublishRestockedForSmallIncrease() {
        stockEventService.evaluateQuantityChange(sampleProduct(), 10, 13);

        verify(stockEventPublisher, never()).publish(argThat(event ->
                StockEvent.TYPE_RESTOCKED.equals(event.eventType())));
    }

    private static Product sampleProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setFarmerId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        product.setCropName("Tomato");
        product.setQuantity(10);
        product.setUnitPrice(new BigDecimal("100.00"));
        product.setHarvestDate(LocalDate.of(2026, 8, 1));
        product.setLocation("Kandy");
        product.setActive(true);
        return product;
    }
}