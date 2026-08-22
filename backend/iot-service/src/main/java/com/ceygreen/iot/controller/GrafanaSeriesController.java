package com.ceygreen.iot.controller;

import com.ceygreen.iot.model.SensorReading;
import com.ceygreen.iot.service.ReadingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Grafana Infinity datasource reads this JSON. Same rows the IoT service stored
 * in Firebase (or in memory when Firebase is off).
 */
@RestController
@RequestMapping("/iot/grafana")
@Tag(name = "Grafana", description = "Time-series export for the Greenhouse Health dashboard.")
@SecurityRequirement(name = "apiKey")
public class GrafanaSeriesController {

    private final ReadingService readingService;

    public GrafanaSeriesController(ReadingService readingService) {
        this.readingService = readingService;
    }

    @GetMapping("/series")
    @Operation(summary = "Sensor history", description = "Last 500 readings for the farmer's greenhouse, one row per sample.")
    public List<Map<String, Object>> series(
            @RequestParam String greenhouseId,
            @RequestParam String farmerId) {
        List<SensorReading> readings = readingService.series(greenhouseId, farmerId);
        List<Map<String, Object>> rows = new ArrayList<>();
        for (SensorReading reading : readings) {
            rows.add(Map.of(
                    "time", reading.getTimestamp() != null ? reading.getTimestamp() : "",
                    "zoneId", reading.getZoneId() != null ? reading.getZoneId() : "ZONE1",
                    "temperature", reading.getTemperature(),
                    "humidity", reading.getHumidity(),
                    "soilMoisture", reading.getSoilMoisture(),
                    "n", reading.getN(),
                    "p", reading.getP(),
                    "k", reading.getK()));
        }
        return rows;
    }
}
