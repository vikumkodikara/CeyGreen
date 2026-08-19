package com.ceygreen.iot.service;

import com.ceygreen.iot.dto.UpdateThresholdRequest;
import com.ceygreen.iot.model.ZoneThresholds;
import com.ceygreen.iot.repository.TelemetryRepository;
import org.springframework.stereotype.Service;

/**
 * Updates per-zone rule limits from the web app.
 */
@Service
public class ThresholdService {

    private final TelemetryRepository telemetryRepository;

    public ThresholdService(TelemetryRepository telemetryRepository) {
        this.telemetryRepository = telemetryRepository;
    }

    public ZoneThresholds update(String zoneId, UpdateThresholdRequest request) {
        telemetryRepository.findGreenhouse(request.getGreenhouseId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Greenhouse not found: " + request.getGreenhouseId()));

        ZoneThresholds thresholds = new ZoneThresholds();
        thresholds.setMaxTemperature(request.getMaxTemperature());
        thresholds.setUrgentMaxTemperature(request.getUrgentMaxTemperature());
        thresholds.setMinTemperature(request.getMinTemperature());
        thresholds.setMinSoilMoisture(request.getMinSoilMoisture());
        thresholds.setUrgentMinSoilMoisture(request.getUrgentMinSoilMoisture());
        thresholds.setMaxSoilMoisture(request.getMaxSoilMoisture());
        thresholds.setMaxHumidity(request.getMaxHumidity());
        thresholds.setMinHumidity(request.getMinHumidity());
        thresholds.setMinNitrogen(request.getMinNitrogen());
        thresholds.setMinPhosphorus(request.getMinPhosphorus());
        thresholds.setMinPotassium(request.getMinPotassium());

        return telemetryRepository.updateThresholds(
                request.getGreenhouseId(),
                zoneId,
                thresholds);
    }
}
