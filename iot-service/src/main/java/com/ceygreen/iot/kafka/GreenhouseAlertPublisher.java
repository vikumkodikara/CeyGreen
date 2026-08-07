package com.ceygreen.iot.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes severe greenhouse alerts to Kafka for Student 6.
 * Broker failures must not fail the ESP32 / client HTTP response.
 */
@Component
public class GreenhouseAlertPublisher {

    private static final Logger log = LoggerFactory.getLogger(GreenhouseAlertPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaAlertProperties properties;

    public GreenhouseAlertPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            KafkaAlertProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    public void publish(GreenhouseAlertEvent event) {
        String key = event.getGreenhouseId() + ":" + event.getZoneId();
        try {
            kafkaTemplate.send(properties.getAlertTopic(), key, event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.warn("Failed to publish greenhouse-alerts for {}: {}",
                                    key, ex.getMessage());
                        } else {
                            log.info("Published greenhouse-alerts for {}", key);
                        }
                    });
        } catch (Exception ex) {
            log.warn("Failed to publish greenhouse-alerts for {}: {}", key, ex.getMessage());
        }
    }
}
