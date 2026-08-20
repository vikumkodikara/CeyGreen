package com.ceygreen.iot.controller;

import com.ceygreen.iot.common.ApiException;
import com.ceygreen.iot.dto.SensorReadingRequest;
import com.ceygreen.iot.dto.SensorReadingResponse;
import com.ceygreen.iot.service.ReadingService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/iot/readings")
@Tag(name = "Readings", description = "ESP32 ingest and latest live reading.")
@SecurityRequirement(name = "apiKey")
public class ReadingController {

    private final ReadingService readingService;

    public ReadingController(ReadingService readingService) {
        this.readingService = readingService;
    }

    @Hidden
    @GetMapping
    public SensorReadingResponse listNotSupported() {
        throw new ApiException(
                HttpStatus.METHOD_NOT_ALLOWED,
                "There is no list URL. Use GET /api/iot/readings/GH001/latest");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Ingest reading", description = "Writes through to Firebase and runs the rule engine.")
    public SensorReadingResponse ingest(@Valid @RequestBody SensorReadingRequest request) {
        return readingService.ingest(request);
    }

    @GetMapping("/{greenhouseId}/latest")
    @Operation(summary = "Latest reading", description = "Dashboard meters for one greenhouse.")
    public SensorReadingResponse latest(@PathVariable String greenhouseId) {
        return readingService.latest(greenhouseId);
    }

    @Hidden
    @PostMapping("/{greenhouseId}/latest")
    public SensorReadingResponse latestViaPost(@PathVariable String greenhouseId) {
        throw new ApiException(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Use GET on this URL. To send a reading, POST JSON to /api/iot/readings (no /latest)");
    }
}
