package com.ceygreen.iot.repository;

import com.ceygreen.iot.model.Greenhouse;
import com.ceygreen.iot.model.SensorReading;
import com.ceygreen.iot.model.Suggestion;
import com.ceygreen.iot.model.Zone;
import com.ceygreen.iot.model.ZoneThresholds;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Firebase Realtime Database storage when {@code ceygreen.firebase.enabled=true}.
 * Uses the REST API so Docker can reach Firebase over HTTPS.
 *
 * <pre>
 * /greenhouses/{id}/zones/{zoneId}/readings/{timestamp}
 * /greenhouses/{id}/zones/{zoneId}/suggestions/{timestamp}
 * </pre>
 */
@Repository
@ConditionalOnProperty(prefix = "ceygreen.firebase", name = "enabled", havingValue = "true")
public class FirebaseRealtimeRepository implements TelemetryRepository {

    private static final TypeReference<Map<String, Zone>> ZONES = new TypeReference<>() {};
    private static final TypeReference<Map<String, SensorReading>> READINGS = new TypeReference<>() {};
    private static final TypeReference<Map<String, Suggestion>> SUGGESTIONS = new TypeReference<>() {};

    private final RestClient firebaseRestClient;
    private final ObjectMapper objectMapper;

    public FirebaseRealtimeRepository(
            @Qualifier("firebaseRestClient") RestClient firebaseRestClient,
            ObjectMapper objectMapper) {
        this.firebaseRestClient = firebaseRestClient;
        this.objectMapper = objectMapper.copy()
                .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Greenhouse saveGreenhouse(Greenhouse greenhouse) {
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("id", greenhouse.getId());
        root.put("name", greenhouse.getName());
        root.put("farmerId", greenhouse.getFarmerId());
        root.put("createdAt", greenhouse.getCreatedAt());
        patch("/greenhouses/{id}.json", root, greenhouse.getId());

        if (greenhouse.getZones() != null) {
            for (Zone zone : greenhouse.getZones().values()) {
                Map<String, Object> zoneMeta = new LinkedHashMap<>();
                zoneMeta.put("zoneId", zone.getZoneId());
                zoneMeta.put("zoneName", zone.getZoneName());
                zoneMeta.put("cropType", zone.getCropType());
                patch("/greenhouses/{id}/zones/{zoneId}.json", zoneMeta, greenhouse.getId(), zone.getZoneId());
                if (zone.getThresholds() != null) {
                    put("/greenhouses/{id}/zones/{zoneId}/thresholds.json",
                            zone.getThresholds(), greenhouse.getId(), zone.getZoneId());
                }
                if (zone.getDevices() != null) {
                    for (var device : zone.getDevices().entrySet()) {
                        put("/greenhouses/{id}/zones/{zoneId}/devices/{deviceId}.json",
                                device.getValue(),
                                greenhouse.getId(),
                                zone.getZoneId(),
                                device.getKey());
                    }
                }
            }
        }
        return greenhouse;
    }

    @Override
    public Optional<Greenhouse> findGreenhouse(String greenhouseId) {
        Greenhouse greenhouse = get("/greenhouses/{id}.json", Greenhouse.class, greenhouseId);
        if (greenhouse == null) {
            return Optional.empty();
        }
        if (greenhouse.getId() == null) {
            greenhouse.setId(greenhouseId);
        }
        return Optional.of(greenhouse);
    }

    @Override
    public SensorReading saveReading(SensorReading reading) {
        String key = reading.getTimestamp() != null
                ? reading.getTimestamp().replace(".", "_").replace(":", "-")
                : String.valueOf(System.currentTimeMillis());
        put("/greenhouses/{id}/zones/{zoneId}/readings/{key}.json",
                reading, reading.getGreenhouseId(), reading.getZoneId(), key);
        return reading;
    }

    @Override
    public Optional<SensorReading> findLatestReading(String greenhouseId) {
        Map<String, Zone> zones = get("/greenhouses/{id}/zones.json", ZONES, greenhouseId);
        if (zones == null || zones.isEmpty()) {
            return Optional.empty();
        }
        SensorReading latest = null;
        for (var zoneEntry : zones.entrySet()) {
            Map<String, SensorReading> readings = get(
                    "/greenhouses/{id}/zones/{zoneId}/readings.json",
                    READINGS,
                    greenhouseId,
                    zoneEntry.getKey());
            if (readings == null) {
                continue;
            }
            for (SensorReading candidate : readings.values()) {
                if (candidate == null) {
                    continue;
                }
                if (latest == null
                        || (candidate.getTimestamp() != null
                        && latest.getTimestamp() != null
                        && candidate.getTimestamp().compareTo(latest.getTimestamp()) > 0)) {
                    latest = candidate;
                }
            }
        }
        return Optional.ofNullable(latest);
    }

    @Override
    public List<Suggestion> saveSuggestions(String greenhouseId, String zoneId, List<Suggestion> suggestions) {
        delete("/greenhouses/{id}/zones/{zoneId}/suggestions.json", greenhouseId, zoneId);
        int index = 0;
        for (Suggestion suggestion : suggestions) {
            String raw = suggestion.getId() != null
                    ? suggestion.getId()
                    : String.valueOf(System.currentTimeMillis());
            String key = raw.replace(".", "_").replace(":", "-") + "-" + index++;
            put("/greenhouses/{id}/zones/{zoneId}/suggestions/{key}.json",
                    suggestion, greenhouseId, zoneId, key);
        }
        return List.copyOf(suggestions);
    }

    @Override
    public List<Suggestion> findSuggestions(String greenhouseId) {
        Map<String, Zone> zones = get("/greenhouses/{id}/zones.json", ZONES, greenhouseId);
        List<Suggestion> result = new ArrayList<>();
        if (zones == null) {
            return result;
        }
        for (String zoneId : zones.keySet()) {
            Map<String, Suggestion> suggestions = get(
                    "/greenhouses/{id}/zones/{zoneId}/suggestions.json",
                    SUGGESTIONS,
                    greenhouseId,
                    zoneId);
            if (suggestions != null) {
                result.addAll(suggestions.values());
            }
        }
        return result;
    }

    @Override
    public ZoneThresholds updateThresholds(String greenhouseId, String zoneId, ZoneThresholds thresholds) {
        Greenhouse greenhouse = findGreenhouse(greenhouseId)
                .orElseThrow(() -> new IllegalArgumentException("Greenhouse not found: " + greenhouseId));
        Zone zone = greenhouse.getZones() != null ? greenhouse.getZones().get(zoneId) : null;
        if (zone == null) {
            throw new IllegalArgumentException("Zone not found: " + zoneId);
        }
        zone.setThresholds(thresholds);
        put("/greenhouses/{id}/zones/{zoneId}/thresholds.json", thresholds, greenhouseId, zoneId);
        return thresholds;
    }

    @Override
    public Optional<ZoneThresholds> findThresholds(String greenhouseId, String zoneId) {
        ZoneThresholds thresholds = get(
                "/greenhouses/{id}/zones/{zoneId}/thresholds.json",
                ZoneThresholds.class,
                greenhouseId,
                zoneId);
        return Optional.ofNullable(thresholds);
    }

    private <T> T get(String path, Class<T> type, Object... uriVars) {
        try {
            return firebaseRestClient.get()
                    .uri(path, uriVars)
                    .retrieve()
                    .body(type);
        } catch (RestClientException ex) {
            throw wrap(ex);
        }
    }

    private <T> T get(String path, TypeReference<T> type, Object... uriVars) {
        try {
            JsonNode node = firebaseRestClient.get()
                    .uri(path, uriVars)
                    .retrieve()
                    .body(JsonNode.class);
            if (node == null || node.isNull() || node.isMissingNode()) {
                return null;
            }
            return objectMapper.convertValue(node, type);
        } catch (RestClientException | IllegalArgumentException ex) {
            throw wrap(ex);
        }
    }

    private void put(String path, Object body, Object... uriVars) {
        try {
            firebaseRestClient.put()
                    .uri(path, uriVars)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw wrap(ex);
        }
    }

    private void patch(String path, Object body, Object... uriVars) {
        try {
            firebaseRestClient.patch()
                    .uri(path, uriVars)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw wrap(ex);
        }
    }

    private void delete(String path, Object... uriVars) {
        try {
            firebaseRestClient.delete()
                    .uri(path, uriVars)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw wrap(ex);
        }
    }

    private static IllegalStateException wrap(Exception ex) {
        if (ex instanceof ResourceAccessException) {
            return new IllegalStateException("Firebase request timed out", ex);
        }
        if (ex instanceof RestClientResponseException responseEx) {
            return new IllegalStateException(
                    "Firebase HTTP " + responseEx.getStatusCode().value() + ": " + responseEx.getStatusText(),
                    ex);
        }
        return new IllegalStateException("Firebase request failed: " + ex.getMessage(), ex);
    }
}
