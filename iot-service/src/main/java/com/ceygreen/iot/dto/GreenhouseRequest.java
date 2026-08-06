package com.ceygreen.iot.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record GreenhouseRequest(
        @NotBlank String name,
        @NotBlank String farmerId,
        List<ZoneRequest> zones) {

    public record ZoneRequest(
            @NotBlank String zoneName,
            @NotBlank String cropType) {
    }
}
