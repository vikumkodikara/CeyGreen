package com.ceygreen.ecommerce.kafka;

import com.ceygreen.ecommerce.dto.OrderEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public OrderEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${ceygreen.kafka.order-events-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publishOrderCreated(OrderEvent event) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventId", event.eventId().toString());
        payload.put("orderId", event.orderId());
        payload.put("buyerId", event.buyerId().toString());
        payload.put("farmerId", event.farmerId().toString());
        payload.put("productId", event.productId());
        payload.put("cropName", event.cropName());
        payload.put("quantity", event.quantity());
        payload.put("unitPrice", event.unitPrice());
        payload.put("totalPrice", event.totalPrice());
        payload.put("status", event.status().name());
        payload.put("orderedAt", event.orderedAt().toString());
        payload.put("eventType", event.eventType());

        kafkaTemplate.send(topic, event.orderId().toString(), payload);
        log.info("Published order event: orderId={}, eventType={}", event.orderId(), event.eventType());
    }
}