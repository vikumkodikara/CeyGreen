package com.ceygreen.iot.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/** Represents a greenhouse blueprint with its zones and device mappings. */
public class Greenhouse {

    private String id;
    private String name;
    private String farmerId;
    private Instant createdAt;
    private List<Zone> zones;

    public Greenhouse() {}

    public Greenhouse(String id, String name, String farmerId) {
        this.id = id;
        this.name = name;
        this.farmerId = farmerId;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<Zone> getZones() { return zones; }
    public void setZones(List<Zone> zones) { this.zones = zones; }
}
