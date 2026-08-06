package com.ceygreen.analytics.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

/** Consumes diagnosis-events published by Student 2's Disease Detection service. */
@Component
public class DiagnosisEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(DiagnosisEventConsumer.class);

    @KafkaListener(topics = "diagnosis-events", groupId = "analytics-notification-group")
    public void consume(Map<String, Object> event) {
        log.info("Consumed diagnosis event: {}", event);
        // TODO: Log diagnosis activity and optionally create notification
    }
}
