package com.ceygreen.treatment_service.dto;
import java.util.List;

public record TreatmentResponse(
        Long id,
        String diseaseName,
        String productName,
        String type,
        String dosage,
        String frequency,
        String safetyNotes,
        String cropType,
        String severity,
        Integer phiDays,
        String applicationMethod,
        String brandNames,
        Integer effectivenessScore,
        Double averageRating,
        List<RatingResponse> reviews,
        boolean active) {
}