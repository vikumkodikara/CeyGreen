package com.ceygreen.treatment_service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor

public class TreatmentEventProducer {
    @org.springframework.beans.factory.annotation.Value("${KAFKA_TREATMENT_EVENTS_TOPIC:treatment-events}")
    private String topic;

    private final KafkaTemplate<String, TreatmentEvent> kafkaTemplate;

    public void publish(TreatmentEvent event) {
        kafkaTemplate.send(topic, event.diseaseName(), event);
    }

}
