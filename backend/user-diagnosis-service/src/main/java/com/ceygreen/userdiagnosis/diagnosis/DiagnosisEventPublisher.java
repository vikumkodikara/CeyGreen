package com.ceygreen.userdiagnosis.diagnosis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes a diagnosis-events message and moves on. A broker outage must not fail the
 * client response — Student 6 consumes whenever ready; the producer has no availability
 * dependency on the consumer.
 */
@Component
public class DiagnosisEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(DiagnosisEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaDiagnosisProperties properties;

    public DiagnosisEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                   KafkaDiagnosisProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    public void publish(Diagnosis diagnosis) {
        DiagnosisEvent event = new DiagnosisEvent(
                diagnosis.getId(),
                diagnosis.getFarmerId(),
                diagnosis.getPredictedDisease(),
                diagnosis.getConfidenceScore(),
                diagnosis.getTimestamp());
        try {
            kafkaTemplate.send(properties.getDiagnosisTopic(), diagnosis.getId(), (Object) event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.warn("Failed to publish diagnosis-events for {}: {}",
                                    diagnosis.getId(), ex.getMessage());
                        } else {
                            log.info("Published diagnosis-events for {}", diagnosis.getId());
                        }
                    });
        } catch (Exception ex) {
            log.warn("Failed to publish diagnosis-events for {}: {}", diagnosis.getId(), ex.getMessage());
        }
    }
}
