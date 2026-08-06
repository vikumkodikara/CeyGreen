package com.ceygreen.iot.service;

import com.ceygreen.iot.dto.SensorReadingRequest;
import com.ceygreen.iot.dto.SensorReadingResponse;
import com.ceygreen.iot.model.Greenhouse;
import com.ceygreen.iot.model.SensorReading;
import com.ceygreen.iot.repository.TelemetryRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Ingests ESP32 sensor readings. Rule-engine evaluation is added in a later step.
 */
@Service
public class ReadingService {

    private final TelemetryRepository telemetryRepository;

    public ReadingService(TelemetryRepository telemetryRepository) {
        this.telemetryRepository = telemetryRepository;
    }

    public SensorReadingResponse ingest(SensorReadingRequest request) {
        Greenhouse greenhouse = telemetryRepository.findGreenhouse(request.getGreenhouseId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Greenhouse not found: " + request.getGreenhouseId()));

        if (!greenhouse.getZones().containsKey(request.getZoneId())) {
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
        return SensorReadingResponse.from(saved);
    }
}
