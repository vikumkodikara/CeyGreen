package com.ceygreen.iot.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Top-level greenhouse blueprint stored under {@code /greenhouses/{id}} in Firebase.
 * Timestamps are ISO-8601 strings because Firebase cannot deserialize {@code java.time.Instant}.
 */
public class Greenhouse {

    private String id;
    private String name;
    private String farmerId;
    private String createdAt;
    private Map<String, Zone> zones = new LinkedHashMap<>();

    public Greenhouse() {
    }

    public Greenhouse(String id, String name, String farmerId, String createdAt) {
        this.id = id;
        this.name = name;
        this.farmerId = farmerId;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, Zone> getZones() {
        return zones;
    }

    public void setZones(Map<String, Zone> zones) {
        this.zones = zones != null ? zones : new LinkedHashMap<>();
    }
}
