package com.ceygreen.iot.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Publishes urgent greenhouse alerts to Kafka when a sensor reading crosses a threshold.
 * The Analytics & Notification service (Student 6) consumes these events.
 */
@Component
public class GreenhouseAlertPublisher {

    private static final Logger log = LoggerFactory.getLogger(GreenhouseAlertPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public GreenhouseAlertPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                    @Value("${ceygreen.kafka.greenhouse-alerts-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publishAlert(String greenhouseId, String zoneId, String alertType, String message) {
        Map<String, Object> event = Map.of(
                "greenhouseId", greenhouseId,
                "zoneId", zoneId,
                "alertType", alertType,
                "message", message,
                "timestamp", java.time.Instant.now().toString()
        );
        kafkaTemplate.send(topic, greenhouseId, event);
        log.info("Published greenhouse alert: type={}, greenhouse={}, zone={}", alertType, greenhouseId, zoneId);
    }
}
