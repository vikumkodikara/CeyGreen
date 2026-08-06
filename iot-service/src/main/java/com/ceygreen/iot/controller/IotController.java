package com.ceygreen.iot.controller;

import com.ceygreen.iot.dto.GreenhouseRequest;
import com.ceygreen.iot.dto.ReadingRequest;
import com.ceygreen.iot.dto.SuggestionResponse;
import com.ceygreen.iot.dto.ThresholdUpdateRequest;
import com.ceygreen.iot.service.IotService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for IoT telemetry and control operations.
 * All traffic arrives through the API Gateway.
 */
@RestController
@RequestMapping("/iot")
public class IotController {

    private final IotService iotService;

    public IotController(IotService iotService) {
        this.iotService = iotService;
    }

    /** Register a new greenhouse blueprint with optional zones. */
    @PostMapping("/greenhouses")
    public ResponseEntity<Map<String, String>> registerGreenhouse(@Valid @RequestBody GreenhouseRequest request) {
        String id = iotService.registerGreenhouse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
    }

    /** Ingest an hourly sensor reading from an ESP32 device. */
    @PostMapping("/readings")
    public ResponseEntity<Void> ingestReading(@Valid @RequestBody ReadingRequest request) {
        iotService.ingestReading(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** Get current recommendations per zone for a greenhouse. */
    @GetMapping("/suggestions/{greenhouseId}")
    public ResponseEntity<SuggestionResponse> getSuggestions(@PathVariable String greenhouseId) {
        return ResponseEntity.ok(iotService.getSuggestions(greenhouseId));
    }

    /** Adjust rule-engine thresholds for a zone. */
    @PutMapping("/thresholds/{zoneId}")
    public ResponseEntity<Void> updateThresholds(@PathVariable String zoneId,
                                                  @Valid @RequestBody ThresholdUpdateRequest request) {
        iotService.updateThresholds(zoneId, request);
        return ResponseEntity.ok().build();
    }
}
