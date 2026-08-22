package com.ceygreen.iot.service;

import com.ceygreen.iot.common.ApiException;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

        Optional<Greenhouse> existing = telemetryRepository.findGreenhouse(greenhouseId);
        if (existing.isPresent()) {
            Greenhouse found = existing.get();
            String owner = found.getFarmerId();
            if (owner != null && owner.equals(request.getFarmerId().trim())) {
                return GreenhouseResponse.from(found);
            }
            throw ApiException.conflict("Greenhouse ID already registered to another farmer");
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

    public List<GreenhouseResponse> listMine(String farmerId) {
        if (farmerId == null || farmerId.isBlank()) {
            throw ApiException.badRequest("farmerId is required");
        }
        return telemetryRepository.findByFarmerId(farmerId.trim()).stream()
                .map(GreenhouseResponse::from)
                .toList();
    }

    public void remove(String greenhouseId, String farmerId) {
        Greenhouse greenhouse = telemetryRepository.findGreenhouse(greenhouseId)
                .orElseThrow(() -> new IllegalArgumentException("Greenhouse not found: " + greenhouseId));
        GreenhouseOwnership.requireOwner(greenhouse, farmerId);
        telemetryRepository.deleteGreenhouse(greenhouseId);
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
