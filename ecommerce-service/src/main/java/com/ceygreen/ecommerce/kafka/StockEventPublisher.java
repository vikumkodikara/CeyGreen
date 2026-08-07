package com.ceygreen.ecommerce.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class StockEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(StockEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public StockEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                @Value("${ceygreen.kafka.stock-events-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publishStockLow(Long productId, String productName, int remainingQuantity) {
        kafkaTemplate.send(topic, productId.toString(), Map.of(
                "productId", productId, "productName", productName, "remainingQuantity", remainingQuantity,
                "event", "STOCK_LOW", "timestamp", java.time.Instant.now().toString()));
        log.info("Published stock event: productId={}, remaining={}", productId, remainingQuantity);
    }
}
