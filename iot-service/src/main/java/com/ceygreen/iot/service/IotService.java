package com.ceygreen.iot.service;

import com.ceygreen.iot.dto.GreenhouseRequest;
import com.ceygreen.iot.dto.ReadingRequest;
import com.ceygreen.iot.dto.SuggestionResponse;
import com.ceygreen.iot.dto.ThresholdUpdateRequest;
import com.ceygreen.iot.kafka.GreenhouseAlertPublisher;
import com.ceygreen.iot.model.Greenhouse;
import com.ceygreen.iot.model.SensorReading;
import com.ceygreen.iot.model.Suggestion;
import com.ceygreen.iot.model.Zone;
import com.ceygreen.iot.repository.FirebaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Business logic for greenhouse IoT operations: blueprint registration, reading ingestion,
 * suggestion generation, and threshold management.
 */
@Service
public class IotService {

    private static final Logger log = LoggerFactory.getLogger(IotService.class);

    private final FirebaseRepository firebaseRepository;
    private final GreenhouseAlertPublisher alertPublisher;

    public IotService(FirebaseRepository firebaseRepository, GreenhouseAlertPublisher alertPublisher) {
        this.firebaseRepository = firebaseRepository;
        this.alertPublisher = alertPublisher;
    }

    public String registerGreenhouse(GreenhouseRequest request) {
        String id = UUID.randomUUID().toString();
        Greenhouse greenhouse = new Greenhouse(id, request.name(), request.farmerId());

        if (request.zones() != null) {
            List<Zone> zones = request.zones().stream()
                    .map(z -> new Zone(UUID.randomUUID().toString(), z.zoneName(), z.cropType()))
                    .collect(Collectors.toList());
            greenhouse.setZones(zones);
        }

        firebaseRepository.saveGreenhouse(greenhouse);
        log.info("Registered greenhouse '{}' with id={}", request.name(), id);
        return id;
    }

    public void ingestReading(ReadingRequest request) {
        SensorReading reading = new SensorReading();
        reading.setGreenhouseId(request.greenhouseId());
        reading.setZoneId(request.zoneId());
        reading.setTemperature(request.temperature());
        reading.setHumidity(request.humidity());
        reading.setSoilMoisture(request.soilMoisture());
        reading.setNitrogen(request.nitrogen());
        reading.setPhosphorus(request.phosphorus());
        reading.setPotassium(request.potassium());
        reading.setTimestamp(Instant.now());

        firebaseRepository.saveReading(request.greenhouseId(), request.zoneId(), reading);

        // TODO: Evaluate against zone thresholds and generate suggestions/alerts
        log.info("Ingested reading for greenhouse={}, zone={}", request.greenhouseId(), request.zoneId());
    }

    public SuggestionResponse getSuggestions(String greenhouseId) {
        List<Suggestion> suggestions = firebaseRepository.getSuggestions(greenhouseId);
        return new SuggestionResponse(greenhouseId, suggestions);
    }

    public void updateThresholds(String zoneId, ThresholdUpdateRequest request) {
        firebaseRepository.updateThresholds(zoneId, request.thresholds());
        log.info("Updated thresholds for zone={}", zoneId);
    }
}
