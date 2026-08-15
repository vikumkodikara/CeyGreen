package com.ceygreen.iot.repository;

import com.ceygreen.iot.model.Greenhouse;
import com.ceygreen.iot.model.SensorReading;
import com.ceygreen.iot.model.Suggestion;
import com.ceygreen.iot.model.ZoneThresholds;

import java.util.List;
import java.util.Optional;

/**
 * Storage contract for greenhouse telemetry.
 * Implementations: in-memory (local demo) and Firebase Realtime Database.
 */
public interface TelemetryRepository {

    Greenhouse saveGreenhouse(Greenhouse greenhouse);

    Optional<Greenhouse> findGreenhouse(String greenhouseId);

    SensorReading saveReading(SensorReading reading);

    List<Suggestion> saveSuggestions(String greenhouseId, String zoneId, List<Suggestion> suggestions);

    List<Suggestion> findSuggestions(String greenhouseId);

    ZoneThresholds updateThresholds(String greenhouseId, String zoneId, ZoneThresholds thresholds);

    Optional<ZoneThresholds> findThresholds(String greenhouseId, String zoneId);
}
