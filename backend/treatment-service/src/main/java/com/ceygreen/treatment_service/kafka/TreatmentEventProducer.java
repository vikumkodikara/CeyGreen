package com.ceygreen.treatment_service.kafka;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TreatmentEventProducer {

    private static final Logger log = LoggerFactory.getLogger(TreatmentEventProducer.class);

    @Value("${KAFKA_TREATMENT_EVENTS_TOPIC:treatment-events}")
    private String topic;

    private final KafkaTemplate<String, TreatmentEvent> kafkaTemplate;

    public void publish(TreatmentEvent event) {
        try {
            kafkaTemplate.send(topic, event.diseaseName(), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.warn("Failed to publish treatment event for {}: {}",
                                    event.diseaseName(), ex.getMessage());
                        } else {
                            log.info("Published treatment event for {}", event.diseaseName());
                        }
                    });
        } catch (Exception ex) {
            log.warn("Failed to publish treatment event for {}: {}", event.diseaseName(), ex.getMessage());
        }
    }
}
