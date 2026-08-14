package com.ceygreen.treatment_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TreatmentRequest(
        @NotBlank(message = "Disease name is required") String diseaseName,
        @NotBlank(message = "Product name is required") String productName,
        @NotBlank(message = "Type is required") String type,
        String dosage,
        String frequency,
        String safetyNotes,
        String cropType,
        String severity,
        @NotNull(message = "Active status is required") Boolean active) {
}