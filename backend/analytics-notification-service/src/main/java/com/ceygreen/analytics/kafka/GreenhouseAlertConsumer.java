package com.ceygreen.analytics.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

/** Consumes greenhouse-alerts published by Student 1's IoT service. */
@Component
public class GreenhouseAlertConsumer {
    private static final Logger log = LoggerFactory.getLogger(GreenhouseAlertConsumer.class);

    @KafkaListener(topics = "greenhouse-alerts", groupId = "analytics-notification-group")
    public void consume(Map<String, Object> event) {
        log.info("Consumed greenhouse alert: {}", event);
        // TODO: Create urgent notification for the greenhouse owner
    }
}
