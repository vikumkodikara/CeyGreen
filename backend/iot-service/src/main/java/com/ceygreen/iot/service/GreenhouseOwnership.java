package com.ceygreen.iot.service;

import com.ceygreen.iot.common.ApiException;
import com.ceygreen.iot.model.Greenhouse;

/** Ensures a farmer can only read or change their own greenhouse. */
final class GreenhouseOwnership {

    private GreenhouseOwnership() {
    }

    static void requireOwner(Greenhouse greenhouse, String farmerId) {
        if (farmerId == null || farmerId.isBlank()) {
            throw ApiException.badRequest("farmerId is required");
        }
        String owner = greenhouse.getFarmerId();
        if (owner == null || !owner.equals(farmerId.trim())) {
            throw ApiException.forbidden("This greenhouse belongs to another farmer");
        }
    }
}
