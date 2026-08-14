package com.ceygreen.treatment_service.dto;

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
        boolean active) {
}