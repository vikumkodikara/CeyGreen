package com.ceygreen.iot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReadingRequest(
        @NotBlank String greenhouseId,
        @NotBlank String zoneId,
        @NotNull Double temperature,
        @NotNull Double humidity,
        @NotNull Double soilMoisture,
        @NotNull Double nitrogen,
        @NotNull Double phosphorus,
        @NotNull Double potassium) {
}
