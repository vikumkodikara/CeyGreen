package com.ceygreen.diagnosis.diagnosis;

import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes a diagnosis-events message and moves on asynchronously. A broker outage must not
 * block or fail the client response — the producer has no availability dependency on Kafka.
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

        CompletableFuture.runAsync(() -> {
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
                log.warn("Failed to dispatch diagnosis-events for {}: {}", diagnosis.getId(), ex.getMessage());
            }
        });
    }
}
