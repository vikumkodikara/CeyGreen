package com.ceygreen.treatment.dto;

public record TreatmentUpdateRequest(
        String productName,
        String type,
        String dosage,
        String frequency,
        String safetyNotes,
        Boolean active) {
}
