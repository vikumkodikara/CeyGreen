package com.ceygreen.treatment_service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class TreatmentEventProducer {
    private static final String TOPIC = "treatment-events";
    private final KafkaTemplate<String, TreatmentEvent> kafkaTemplate;

    public void publish(TreatmentEvent event) {
        kafkaTemplate.send(TOPIC, event.diseaseName(), event);
    }

}
