package com.ceygreen.iot.repository;

import com.ceygreen.iot.model.Greenhouse;
import com.ceygreen.iot.model.SensorReading;
import com.ceygreen.iot.model.Suggestion;
import com.ceygreen.iot.model.Zone;
import com.ceygreen.iot.model.ZoneThresholds;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Local demo storage used when {@code ceygreen.firebase.enabled=false}.
 * Data is lost when the process restarts.
 */
@Repository
@ConditionalOnProperty(prefix = "ceygreen.firebase", name = "enabled", havingValue = "false", matchIfMissing = true)
public class InMemoryTelemetryRepository implements TelemetryRepository {

    private final Map<String, Greenhouse> greenhouses = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();
    private final Map<String, List<Suggestion>> suggestions = new ConcurrentHashMap<>();

    @Override
    public Greenhouse saveGreenhouse(Greenhouse greenhouse) {
        greenhouses.put(greenhouse.getId(), greenhouse);
        return greenhouse;
    }

    @Override
    public Optional<Greenhouse> findGreenhouse(String greenhouseId) {
        return Optional.ofNullable(greenhouses.get(greenhouseId));
    }

    @Override
    public SensorReading saveReading(SensorReading reading) {
        String key = readingKey(reading.getGreenhouseId(), reading.getZoneId());
        readings.computeIfAbsent(key, ignored -> new ArrayList<>()).add(reading);
        return reading;
    }

    @Override
    public Optional<SensorReading> findLatestReading(String greenhouseId) {
        SensorReading latest = null;
        for (Map.Entry<String, List<SensorReading>> entry : readings.entrySet()) {
            if (!entry.getKey().startsWith(greenhouseId + "::") || entry.getValue().isEmpty()) {
                continue;
            }
            SensorReading candidate = entry.getValue().get(entry.getValue().size() - 1);
            if (latest == null
                    || (candidate.getTimestamp() != null
                    && latest.getTimestamp() != null
                    && candidate.getTimestamp().compareTo(latest.getTimestamp()) > 0)) {
                latest = candidate;
            }
        }
        return Optional.ofNullable(latest);
    }

    @Override
    public List<Suggestion> saveSuggestions(String greenhouseId, String zoneId, List<Suggestion> newSuggestions) {
        String key = readingKey(greenhouseId, zoneId);
        suggestions.put(key, new ArrayList<>(newSuggestions));
        return List.copyOf(newSuggestions);
    }

    @Override
    public List<Suggestion> findSuggestions(String greenhouseId) {
        List<Suggestion> result = new ArrayList<>();
        for (Map.Entry<String, List<Suggestion>> entry : suggestions.entrySet()) {
            if (entry.getKey().startsWith(greenhouseId + "::")) {
                result.addAll(entry.getValue());
            }
        }
        return result;
    }

    @Override
    public ZoneThresholds updateThresholds(String greenhouseId, String zoneId, ZoneThresholds thresholds) {
        Greenhouse greenhouse = greenhouses.get(greenhouseId);
        if (greenhouse == null) {
            throw new IllegalArgumentException("Greenhouse not found: " + greenhouseId);
        }
        Zone zone = greenhouse.getZones() != null ? greenhouse.getZones().get(zoneId) : null;
        if (zone == null) {
            throw new IllegalArgumentException("Zone not found: " + zoneId);
        }
        zone.setThresholds(thresholds);
        return thresholds;
    }

    @Override
    public Optional<ZoneThresholds> findThresholds(String greenhouseId, String zoneId) {
        return findGreenhouse(greenhouseId)
                .map(Greenhouse::getZones)
                .map(zones -> zones.get(zoneId))
                .map(Zone::getThresholds);
    }

    private static String readingKey(String greenhouseId, String zoneId) {
        return greenhouseId + "::" + zoneId;
    }
}
