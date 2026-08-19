package com.ceygreen.treatment_service.dto;

public record RatingRequest(
        String farmerId,
        String farmerName,
        Integer rating,
        String comment
) {
}
