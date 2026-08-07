package com.ceygreen.treatment.dto;

import jakarta.validation.constraints.NotBlank;

public record TreatmentRequest(
        @NotBlank String diseaseName,
        @NotBlank String productName,
        @NotBlank String type,
        String dosage,
        String frequency,
        String safetyNotes) {
}
