package com.ceygreen.ecommerce.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class OrderEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                @Value("${ceygreen.kafka.order-events-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publishOrderPlaced(Long orderId, String buyerId, String totalAmount) {
        kafkaTemplate.send(topic, orderId.toString(), Map.of(
                "orderId", orderId, "buyerId", buyerId, "totalAmount", totalAmount,
                "event", "ORDER_PLACED", "timestamp", java.time.Instant.now().toString()));
        log.info("Published order event: orderId={}", orderId);
    }
}
