package com.ceygreen.treatment_service.dto;

import java.time.LocalDateTime;

public record RatingResponse(
        String farmerId,
        String farmerName,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
}
