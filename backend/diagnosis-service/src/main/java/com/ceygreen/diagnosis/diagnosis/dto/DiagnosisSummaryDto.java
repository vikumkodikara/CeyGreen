package com.ceygreen.diagnosis.diagnosis.dto;

import com.ceygreen.diagnosis.diagnosis.Diagnosis;
import java.time.Instant;

/**
 * Lightweight summary DTO for scan history lists.
 * Avoids returning unnecessary data for list views.
 */
public record DiagnosisSummaryDto(
        String diagnosisId,
        String cropType,
        String predictedDisease,
        double confidenceScore,
        String imageUrl,
        Instant timestamp
) {
    public static DiagnosisSummaryDto from(Diagnosis diagnosis) {
        return new DiagnosisSummaryDto(
                diagnosis.getId(),
                diagnosis.getCropType(),
                diagnosis.getPredictedDisease(),
                diagnosis.getConfidenceScore(),
                diagnosis.getImageUrl(),
                diagnosis.getTimestamp());
    }
}
