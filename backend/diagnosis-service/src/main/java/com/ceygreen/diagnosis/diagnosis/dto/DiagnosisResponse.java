package com.ceygreen.diagnosis.diagnosis.dto;

import com.ceygreen.diagnosis.diagnosis.Diagnosis;
import java.time.Instant;
import java.util.UUID;

public record DiagnosisResponse(
        String diagnosisId,
        UUID farmerId,
        String imageUrl,
        String cropType,
        String predictedDisease,
        double confidenceScore,
        Instant timestamp
) {
    public static DiagnosisResponse from(Diagnosis diagnosis) {
        return new DiagnosisResponse(
                diagnosis.getId(),
                diagnosis.getFarmerId(),
                diagnosis.getImageUrl(),
                diagnosis.getCropType(),
                diagnosis.getPredictedDisease(),
                diagnosis.getConfidenceScore(),
                diagnosis.getTimestamp());
    }
}
