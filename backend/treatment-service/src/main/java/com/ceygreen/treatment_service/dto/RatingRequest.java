package com.ceygreen.treatment_service.dto;

public record RatingRequest(
        String farmerId,
        Integer rating
) {
}
