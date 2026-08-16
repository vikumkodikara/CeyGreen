package com.ceygreen.iot.controller;

import com.ceygreen.iot.dto.SensorReadingRequest;
import com.ceygreen.iot.dto.SensorReadingResponse;
import com.ceygreen.iot.service.ReadingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for ESP32 sensor ingest.
 */
@RestController
@RequestMapping("/iot/readings")
public class ReadingController {

    private final ReadingService readingService;

    public ReadingController(ReadingService readingService) {
        this.readingService = readingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorReadingResponse ingest(@Valid @RequestBody SensorReadingRequest request) {
        return readingService.ingest(request);
    }

    @GetMapping("/{greenhouseId}/latest")
    public SensorReadingResponse latest(@PathVariable String greenhouseId) {
        return readingService.latest(greenhouseId);
    }
}
