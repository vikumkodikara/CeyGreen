package com.ceygreen.treatment.dto;

public record TreatmentResponse(
        Long id,
        String diseaseName,
        String productName,
        String type,
        String dosage,
        String frequency,
        String safetyNotes,
        boolean active) {
}
