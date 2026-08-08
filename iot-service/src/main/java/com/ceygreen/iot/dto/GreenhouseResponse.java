package com.ceygreen.iot.dto;

import com.ceygreen.iot.model.Device;
import com.ceygreen.iot.model.Greenhouse;
import com.ceygreen.iot.model.Zone;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Response returned after creating (or reading) a greenhouse blueprint.
 */
public class GreenhouseResponse {

    private String id;
    private String name;
    private String farmerId;
    private Instant createdAt;
    private List<ZoneSummary> zones = new ArrayList<>();

    public static GreenhouseResponse from(Greenhouse greenhouse) {
        GreenhouseResponse response = new GreenhouseResponse();
        response.id = greenhouse.getId();
        response.name = greenhouse.getName();
        response.farmerId = greenhouse.getFarmerId();
        response.createdAt = greenhouse.getCreatedAt();

        for (Zone zone : greenhouse.getZones().values()) {
            ZoneSummary summary = new ZoneSummary();
            summary.zoneId = zone.getZoneId();
            summary.zoneName = zone.getZoneName();
            summary.cropType = zone.getCropType();
            summary.deviceCount = zone.getDevices() != null ? zone.getDevices().size() : 0;
            if (zone.getDevices() != null && !zone.getDevices().isEmpty()) {
                Device first = zone.getDevices().values().iterator().next();
                summary.deviceCode = first.getDeviceCode();
            }
            response.zones.add(summary);
        }
        return response;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<ZoneSummary> getZones() {
        return zones;
    }

    public static class ZoneSummary {
        private String zoneId;
        private String zoneName;
        private String cropType;
        private int deviceCount;
        private String deviceCode;

        public String getZoneId() {
            return zoneId;
        }

        public String getZoneName() {
            return zoneName;
        }

        public String getCropType() {
            return cropType;
        }

        public int getDeviceCount() {
            return deviceCount;
        }

        public String getDeviceCode() {
            return deviceCode;
        }
    }
}
