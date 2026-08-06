package com.ceygreen.iot.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record ThresholdUpdateRequest(
        @NotNull Map<String, Double> thresholds) {
}
