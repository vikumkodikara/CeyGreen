package com.ceygreen.iot.service;

import com.ceygreen.iot.dto.CreateGreenhouseRequest;
import com.ceygreen.iot.dto.GreenhouseResponse;
import com.ceygreen.iot.dto.ZoneRequest;
import com.ceygreen.iot.model.Device;
import com.ceygreen.iot.model.Greenhouse;
import com.ceygreen.iot.model.Zone;
import com.ceygreen.iot.model.ZoneThresholds;
import com.ceygreen.iot.repository.TelemetryRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Creates a greenhouse blueprint with zones and one ESP32 device per zone.
 */
@Service
public class GreenhouseService {

    private final TelemetryRepository telemetryRepository;

    public GreenhouseService(TelemetryRepository telemetryRepository) {
        this.telemetryRepository = telemetryRepository;
    }

    public GreenhouseResponse create(CreateGreenhouseRequest request) {
        String greenhouseId = blankToNull(request.getGreenhouseId());
        if (greenhouseId == null) {
            greenhouseId = "GH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        if (telemetryRepository.findGreenhouse(greenhouseId).isPresent()) {
            throw new IllegalArgumentException("Greenhouse already exists: " + greenhouseId);
        }

        Greenhouse greenhouse = new Greenhouse(
                greenhouseId,
                request.getName(),
                request.getFarmerId(),
                Instant.now().toString());

        Map<String, Zone> zones = new LinkedHashMap<>();
        for (ZoneRequest zoneRequest : request.getZones()) {
            Zone zone = new Zone(zoneRequest.getZoneId(), zoneRequest.getZoneName(), zoneRequest.getCropType());
            zone.setThresholds(ZoneThresholds.defaults());

            String deviceId = "ESP32-" + zoneRequest.getZoneId();
            String deviceCode = blankToNull(zoneRequest.getDeviceCode());
            if (deviceCode == null) {
                deviceCode = deviceId;
            }
            zone.getDevices().put(deviceId, Device.esp32(deviceId, deviceCode));
            zones.put(zone.getZoneId(), zone);
        }
        greenhouse.setZones(zones);

        Greenhouse saved = telemetryRepository.saveGreenhouse(greenhouse);
        return GreenhouseResponse.from(saved);
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
