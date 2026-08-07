package com.ceygreen.iot.service;

import com.ceygreen.iot.dto.SensorReadingRequest;
import com.ceygreen.iot.dto.SensorReadingResponse;
import com.ceygreen.iot.model.Greenhouse;
import com.ceygreen.iot.model.SensorReading;
import com.ceygreen.iot.model.Suggestion;
import com.ceygreen.iot.model.Zone;
import com.ceygreen.iot.model.ZoneThresholds;
import com.ceygreen.iot.repository.TelemetryRepository;
import com.ceygreen.iot.rule.RuleEngine;
import com.ceygreen.iot.rule.RuleResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Ingests ESP32 sensor readings, evaluates zone rules, and stores suggestions.
 */
@Service
public class ReadingService {

    private final TelemetryRepository telemetryRepository;
    private final RuleEngine ruleEngine;

    public ReadingService(TelemetryRepository telemetryRepository, RuleEngine ruleEngine) {
        this.telemetryRepository = telemetryRepository;
        this.ruleEngine = ruleEngine;
    }

    public SensorReadingResponse ingest(SensorReadingRequest request) {
        Greenhouse greenhouse = telemetryRepository.findGreenhouse(request.getGreenhouseId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Greenhouse not found: " + request.getGreenhouseId()));

        Zone zone = greenhouse.getZones().get(request.getZoneId());
        if (zone == null) {
            throw new IllegalArgumentException("Zone not found: " + request.getZoneId());
        }

        SensorReading reading = new SensorReading();
        reading.setGreenhouseId(request.getGreenhouseId());
        reading.setZoneId(request.getZoneId());
        reading.setTimestamp(Instant.now());
        reading.setTemperature(request.getTemperature());
        reading.setHumidity(request.getHumidity());
        reading.setSoilMoisture(request.getSoilMoisture());
        reading.setN(request.getN());
        reading.setP(request.getP());
        reading.setK(request.getK());

        SensorReading saved = telemetryRepository.saveReading(reading);

        ZoneThresholds thresholds = zone.getThresholds() != null
                ? zone.getThresholds()
                : ZoneThresholds.defaults();

        List<RuleResult> ruleResults = ruleEngine.evaluate(saved, thresholds);
        List<Suggestion> suggestions = new ArrayList<>();
        for (RuleResult result : ruleResults) {
            suggestions.add(Suggestion.of(
                    saved.getGreenhouseId(),
                    zone.getZoneId(),
                    zone.getZoneName(),
                    result.getMessage(),
                    result.getSeverity().name()));
        }
        telemetryRepository.saveSuggestions(saved.getGreenhouseId(), saved.getZoneId(), suggestions);

        return SensorReadingResponse.from(saved);
    }
}
