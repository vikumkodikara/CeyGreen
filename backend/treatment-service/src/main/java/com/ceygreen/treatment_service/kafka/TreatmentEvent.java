package com.ceygreen.treatment_service.kafka;

import java.time.Instant;

public record TreatmentEvent(
        String diseaseName,
        Long treatmentId,
        String productName,
        String severity,
        Instant timestamp) {
}
