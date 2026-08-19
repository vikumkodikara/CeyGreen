package com.ceygreen.iot.controller;

import com.ceygreen.iot.common.ApiException;
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
 * ESP32 ingest and dashboard latest reading.
 * Report CRUD pair: {@code POST /iot/readings}.
 * {@code GET /iot/readings/{id}/latest} is the extra dashboard read (still within the 3–5 rule).
 */
@RestController
@RequestMapping("/iot/readings")
public class ReadingController {

    private final ReadingService readingService;

    public ReadingController(ReadingService readingService) {
        this.readingService = readingService;
    }

    @GetMapping
    public SensorReadingResponse listNotSupported() {
        throw new ApiException(
                HttpStatus.METHOD_NOT_ALLOWED,
                "There is no list URL. Use GET /api/iot/readings/GH001/latest");
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

    @PostMapping("/{greenhouseId}/latest")
    public SensorReadingResponse latestViaPost(@PathVariable String greenhouseId) {
        throw new ApiException(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Use GET on this URL. To send a reading, POST JSON to /api/iot/readings (no /latest)");
    }
}
