package com.ceygreen.ecommerce.kafka;

import com.ceygreen.ecommerce.dto.StockEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class StockEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(StockEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public StockEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${ceygreen.kafka.stock-events-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(StockEvent event) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventId", event.eventId().toString());
        payload.put("productId", event.productId());
        payload.put("farmerId", event.farmerId().toString());
        payload.put("cropName", event.cropName());
        payload.put("previousQuantity", event.previousQuantity());
        payload.put("currentQuantity", event.currentQuantity());
        payload.put("threshold", event.threshold());
        payload.put("eventType", event.eventType());
        payload.put("occurredAt", event.occurredAt().toString());

        try {
            kafkaTemplate.send(topic, event.productId().toString(), payload).get(5, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Failed to publish stock event to topic '" + topic + "' for productId=" + event.productId(),
                    ex);
        }
        log.info("Published {} event: productId={}, quantity={}->{}",
                event.eventType(), event.productId(), event.previousQuantity(), event.currentQuantity());
    }
}