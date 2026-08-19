package com.ceygreen.iot.repository;

import com.ceygreen.iot.model.Greenhouse;
import com.ceygreen.iot.model.SensorReading;
import com.ceygreen.iot.model.Suggestion;
import com.ceygreen.iot.model.Zone;
import com.ceygreen.iot.model.ZoneThresholds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

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
        // Merge metadata only. setValue(greenhouse) would wipe readings/suggestions.
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("id", greenhouse.getId());
        root.put("name", greenhouse.getName());
        root.put("farmerId", greenhouse.getFarmerId());
        root.put("createdAt", greenhouse.getCreatedAt());
        awaitUpdate(greenhouseRef(greenhouse.getId()), root);

        if (greenhouse.getZones() != null) {
            for (Zone zone : greenhouse.getZones().values()) {
                DatabaseReference zoneNode = zoneRef(greenhouse.getId(), zone.getZoneId());
                Map<String, Object> zoneMeta = new LinkedHashMap<>();
                zoneMeta.put("zoneId", zone.getZoneId());
                zoneMeta.put("zoneName", zone.getZoneName());
                zoneMeta.put("cropType", zone.getCropType());
                awaitUpdate(zoneNode, zoneMeta);
                if (zone.getThresholds() != null) {
                    awaitSet(zoneNode.child("thresholds"), zone.getThresholds());
                }
                if (zone.getDevices() != null) {
                    for (var device : zone.getDevices().entrySet()) {
                        awaitSet(zoneNode.child("devices").child(device.getKey()), device.getValue());
                    }
                }
            }
        }
        return greenhouse;
    }

    @Override
    public Optional<Greenhouse> findGreenhouse(String greenhouseId) {
        try {
            DataSnapshot snapshot = awaitGet(greenhouseRef(greenhouseId));
            if (!snapshot.exists()) {
                return Optional.empty();
            }
            Greenhouse greenhouse = snapshot.getValue(Greenhouse.class);
            if (greenhouse != null && greenhouse.getId() == null) {
                greenhouse.setId(greenhouseId);
            }
            return Optional.ofNullable(greenhouse);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Firebase read failed: " + ex.getMessage(), ex);
        }
    }

    @Override
    public SensorReading saveReading(SensorReading reading) {
        String key = reading.getTimestamp() != null
                ? reading.getTimestamp().replace(".", "_").replace(":", "-")
                : String.valueOf(System.currentTimeMillis());
        DatabaseReference ref = zoneRef(reading.getGreenhouseId(), reading.getZoneId())
                .child("readings")
                .child(key);
        awaitSet(ref, reading);
        return reading;
    }

    @Override
    public Optional<SensorReading> findLatestReading(String greenhouseId) {
        DataSnapshot zonesSnap = awaitGet(greenhouseRef(greenhouseId).child("zones"));
        SensorReading latest = null;
        if (!zonesSnap.exists()) {
            return Optional.empty();
        }
        for (DataSnapshot zoneSnap : zonesSnap.getChildren()) {
            for (DataSnapshot readingSnap : zoneSnap.child("readings").getChildren()) {
                SensorReading candidate = readingSnap.getValue(SensorReading.class);
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
        DatabaseReference suggestionsRef = zoneRef(greenhouseId, zoneId).child("suggestions");
        awaitRemove(suggestionsRef);
        int index = 0;
        for (Suggestion suggestion : suggestions) {
            String raw = suggestion.getId() != null
                    ? suggestion.getId()
                    : String.valueOf(System.currentTimeMillis());
            String key = raw.replace(".", "_").replace(":", "-") + "-" + index++;
            awaitSet(suggestionsRef.child(key), suggestion);
        }
        return List.copyOf(suggestions);
    }

    @Override
    public List<Suggestion> findSuggestions(String greenhouseId) {
        DataSnapshot greenhouseSnap = awaitGet(greenhouseRef(greenhouseId).child("zones"));
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
        Zone zone = greenhouse.getZones() != null ? greenhouse.getZones().get(zoneId) : null;
        if (zone == null) {
            throw new IllegalArgumentException("Zone not found: " + zoneId);
        }
        zone.setThresholds(thresholds);
        awaitSet(zoneRef(greenhouseId, zoneId).child("thresholds"), thresholds);
        return thresholds;
    }

    @Override
    public Optional<ZoneThresholds> findThresholds(String greenhouseId, String zoneId) {
        DataSnapshot snapshot = awaitGet(zoneRef(greenhouseId, zoneId).child("thresholds"));
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

    private static void awaitUpdate(DatabaseReference ref, Map<String, Object> values) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<DatabaseError> error = new AtomicReference<>();
        ref.updateChildren(values, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                error.set(databaseError);
            }
            latch.countDown();
        });
        awaitLatch(latch);
        if (error.get() != null) {
            throw new IllegalStateException("Firebase update failed: " + error.get().getMessage());
        }
    }

    private static void awaitSet(DatabaseReference ref, Object value) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<DatabaseError> error = new AtomicReference<>();
        ref.setValue(value, (databaseError, databaseReference) -> {
            if (databaseError != null) {
                error.set(databaseError);
            }
            latch.countDown();
        });
        awaitLatch(latch);
        if (error.get() != null) {
            throw new IllegalStateException("Firebase write failed: " + error.get().getMessage());
        }
    }

    private static void awaitRemove(DatabaseReference ref) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<DatabaseError> error = new AtomicReference<>();
        ref.removeValue((databaseError, databaseReference) -> {
            if (databaseError != null) {
                error.set(databaseError);
            }
            latch.countDown();
        });
        awaitLatch(latch);
        if (error.get() != null) {
            throw new IllegalStateException("Firebase remove failed: " + error.get().getMessage());
        }
    }

    private static DataSnapshot awaitGet(DatabaseReference ref) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<DataSnapshot> snapshot = new AtomicReference<>();
        AtomicReference<DatabaseError> error = new AtomicReference<>();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                snapshot.set(dataSnapshot);
                latch.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                error.set(databaseError);
                latch.countDown();
            }
        });

        awaitLatch(latch);
        if (error.get() != null) {
            throw new IllegalStateException("Firebase read failed: " + error.get().getMessage());
        }
        return snapshot.get();
    }

    private static void awaitLatch(CountDownLatch latch) {
        try {
            if (!latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new TimeoutException("Timed out waiting for Firebase");
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted waiting for Firebase", ex);
        } catch (TimeoutException ex) {
            throw new IllegalStateException("Firebase operation failed", ex);
        }
    }
}
