package com.ceygreen.ecommerce.kafka;

import com.ceygreen.ecommerce.dto.OrderEvent;
import com.ceygreen.ecommerce.entity.Order;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
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
        publishPayload(event.orderId(), toPayload(event));
    }

    public void publishOrderEvent(Order order, String eventType) {
        OrderEvent event = new OrderEvent(
                UUID.randomUUID(),
                order.getId(),
                order.getBuyerId(),
                order.getFarmerId(),
                order.getProductId(),
                order.getCropName(),
                order.getQuantity(),
                order.getUnitPrice(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getOrderedAt(),
                eventType);
        publishPayload(event.orderId(), toPayload(event));
    }

    private void publishPayload(Long orderId, Map<String, Object> payload) {
        kafkaTemplate.send(topic, orderId.toString(), payload);
        log.info("Published order event: orderId={}, eventType={}", orderId, payload.get("eventType"));
    }

    private static Map<String, Object> toPayload(OrderEvent event) {
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
        return payload;
    }
}
