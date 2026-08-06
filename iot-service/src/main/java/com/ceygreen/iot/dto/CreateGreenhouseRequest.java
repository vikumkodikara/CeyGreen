package com.ceygreen.iot.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Body for {@code POST /api/iot/greenhouses}.
 * For your hardware setup, send exactly one zone.
 */
public class CreateGreenhouseRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String farmerId;

    /** Optional. Generated if blank. */
    private String greenhouseId;

    @NotEmpty
    @Valid
    private List<ZoneRequest> zones;

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

    public String getGreenhouseId() {
        return greenhouseId;
    }

    public void setGreenhouseId(String greenhouseId) {
        this.greenhouseId = greenhouseId;
    }

    public List<ZoneRequest> getZones() {
        return zones;
    }

    public void setZones(List<ZoneRequest> zones) {
        this.zones = zones;
    }
}
