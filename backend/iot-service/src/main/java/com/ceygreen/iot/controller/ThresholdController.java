package com.ceygreen.iot.controller;

import com.ceygreen.iot.dto.UpdateThresholdRequest;
import com.ceygreen.iot.model.ZoneThresholds;
import com.ceygreen.iot.service.ThresholdService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for updating zone rule thresholds from the web app.
 */
@RestController
@RequestMapping("/iot/thresholds")
public class ThresholdController {

    private final ThresholdService thresholdService;

    public ThresholdController(ThresholdService thresholdService) {
        this.thresholdService = thresholdService;
    }

    @PutMapping("/{zoneId}")
    public ZoneThresholds update(
            @PathVariable String zoneId,
            @Valid @RequestBody UpdateThresholdRequest request) {
        return thresholdService.update(zoneId, request);
    }
}
