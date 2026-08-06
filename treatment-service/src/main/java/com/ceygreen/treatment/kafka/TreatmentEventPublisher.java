package com.ceygreen.treatment.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class TreatmentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(TreatmentEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public TreatmentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                    @Value("${ceygreen.kafka.treatment-events-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publishSevereTreatment(String diseaseName, String productName, String severity) {
        Map<String, Object> event = Map.of(
                "diseaseName", diseaseName,
                "productName", productName,
                "severity", severity,
                "timestamp", java.time.Instant.now().toString()
        );
        kafkaTemplate.send(topic, diseaseName, event);
        log.info("Published treatment event: disease={}, product={}", diseaseName, productName);
    }
}
