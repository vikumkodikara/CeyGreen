package com.ceygreen.iot.repository;

import com.ceygreen.iot.model.Greenhouse;
import com.ceygreen.iot.model.SensorReading;
import com.ceygreen.iot.model.Suggestion;
import com.ceygreen.iot.model.Zone;
import com.ceygreen.iot.model.ZoneThresholds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Firebase Realtime Database storage when {@code ceygreen.firebase.enabled=true}.
 *
 * <pre>
 * /greenhouses/{id}/zones/{zoneId}/readings/{timestamp}
 * /greenhouses/{id}/zones/{zoneId}/suggestions/{timestamp}
 * </pre>
 */
@Repository
@ConditionalOnProperty(prefix = "ceygreen.firebase", name = "enabled", havingValue = "true")
public class FirebaseRealtimeRepository implements TelemetryRepository {

    private static final long TIMEOUT_SECONDS = 10;

    private final FirebaseDatabase firebaseDatabase;

    public FirebaseRealtimeRepository(FirebaseDatabase firebaseDatabase) {
        this.firebaseDatabase = firebaseDatabase;
    }

    @Override
    public Greenhouse saveGreenhouse(Greenhouse greenhouse) {
        DatabaseReference ref = greenhouseRef(greenhouse.getId());
        await(ref.setValueAsync(greenhouse));
        return greenhouse;
    }

    @Override
    public Optional<Greenhouse> findGreenhouse(String greenhouseId) {
        DataSnapshot snapshot = await(greenhouseRef(greenhouseId).get());
        if (!snapshot.exists()) {
            return Optional.empty();
        }
        return Optional.ofNullable(snapshot.getValue(Greenhouse.class));
    }

    @Override
    public SensorReading saveReading(SensorReading reading) {
        String key = reading.getTimestamp() != null
                ? reading.getTimestamp().toString().replace(".", "_")
                : String.valueOf(System.currentTimeMillis());
        DatabaseReference ref = zoneRef(reading.getGreenhouseId(), reading.getZoneId())
                .child("readings")
                .child(key);
        await(ref.setValueAsync(reading));
        return reading;
    }

    @Override
    public List<Suggestion> saveSuggestions(String greenhouseId, String zoneId, List<Suggestion> suggestions) {
        DatabaseReference suggestionsRef = zoneRef(greenhouseId, zoneId).child("suggestions");
        await(suggestionsRef.removeValueAsync());
        for (Suggestion suggestion : suggestions) {
            String key = suggestion.getId() != null
                    ? suggestion.getId().replace(".", "_").replace(":", "-")
                    : String.valueOf(System.currentTimeMillis());
            await(suggestionsRef.child(key).setValueAsync(suggestion));
        }
        return List.copyOf(suggestions);
    }

    @Override
    public List<Suggestion> findSuggestions(String greenhouseId) {
        DataSnapshot greenhouseSnap = await(greenhouseRef(greenhouseId).child("zones").get());
        List<Suggestion> result = new ArrayList<>();
        if (!greenhouseSnap.exists()) {
            return result;
        }
        for (DataSnapshot zoneSnap : greenhouseSnap.getChildren()) {
            DataSnapshot suggestionsSnap = zoneSnap.child("suggestions");
            for (DataSnapshot suggestionSnap : suggestionsSnap.getChildren()) {
                Suggestion suggestion = suggestionSnap.getValue(Suggestion.class);
                if (suggestion != null) {
                    result.add(suggestion);
                }
            }
        }
        return result;
    }

    @Override
    public ZoneThresholds updateThresholds(String greenhouseId, String zoneId, ZoneThresholds thresholds) {
        Greenhouse greenhouse = findGreenhouse(greenhouseId)
                .orElseThrow(() -> new IllegalArgumentException("Greenhouse not found: " + greenhouseId));
        Zone zone = greenhouse.getZones().get(zoneId);
        if (zone == null) {
            throw new IllegalArgumentException("Zone not found: " + zoneId);
        }
        zone.setThresholds(thresholds);
        await(zoneRef(greenhouseId, zoneId).child("thresholds").setValueAsync(thresholds));
        return thresholds;
    }

    @Override
    public Optional<ZoneThresholds> findThresholds(String greenhouseId, String zoneId) {
        DataSnapshot snapshot = await(zoneRef(greenhouseId, zoneId).child("thresholds").get());
        if (!snapshot.exists()) {
            return Optional.empty();
        }
        return Optional.ofNullable(snapshot.getValue(ZoneThresholds.class));
    }

    private DatabaseReference greenhouseRef(String greenhouseId) {
        return firebaseDatabase.getReference("greenhouses").child(greenhouseId);
    }

    private DatabaseReference zoneRef(String greenhouseId, String zoneId) {
        return greenhouseRef(greenhouseId).child("zones").child(zoneId);
    }

    private static <T> T await(com.google.api.core.ApiFuture<T> future) {
        try {
            return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted waiting for Firebase", ex);
        } catch (ExecutionException | TimeoutException ex) {
            throw new IllegalStateException("Firebase operation failed", ex);
        }
    }
}
