package com.ceygreen.iot.controller;

import com.ceygreen.iot.dto.UpdateThresholdRequest;
import com.ceygreen.iot.model.ZoneThresholds;
import com.ceygreen.iot.service.ThresholdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/iot/thresholds")
@Tag(name = "Thresholds", description = "Hourly suggestion-engine limits for the zone.")
@SecurityRequirement(name = "apiKey")
public class ThresholdController {

    private final ThresholdService thresholdService;

    public ThresholdController(ThresholdService thresholdService) {
        this.thresholdService = thresholdService;
    }

    @PutMapping("/{zoneId}")
    @Operation(summary = "Update zone thresholds", description = "Body must include greenhouseId plus numeric limits.")
    public ZoneThresholds update(
            @PathVariable String zoneId,
            @Valid @RequestBody UpdateThresholdRequest request) {
        return thresholdService.update(zoneId, request);
    }
}
