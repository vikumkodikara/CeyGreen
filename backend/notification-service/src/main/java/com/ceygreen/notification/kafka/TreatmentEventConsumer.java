package com.ceygreen.notification.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

/** Consumes treatment-events published by Student 3's Treatment service. */
@Component
public class TreatmentEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(TreatmentEventConsumer.class);

    @KafkaListener(topics = "treatment-events", groupId = "notification-group")
    public void consume(Map<String, Object> event) {
        log.info("Consumed treatment event: {}", event);
        // TODO: Log severe treatment recommendations and notify farmer
    }
}
