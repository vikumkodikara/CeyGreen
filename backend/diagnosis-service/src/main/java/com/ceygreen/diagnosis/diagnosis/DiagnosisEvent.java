package com.ceygreen.diagnosis.diagnosis;

import java.time.Instant;
import java.util.UUID;

/** Fire-and-forget payload published to the {@code diagnosis-events} Kafka topic. */
public record DiagnosisEvent(
        String diagnosisId,
        UUID farmerId,
        String predictedDisease,
        double confidenceScore,
        Instant timestamp
) {
}
