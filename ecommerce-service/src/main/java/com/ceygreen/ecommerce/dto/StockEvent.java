package com.ceygreen.ecommerce.dto;

import java.time.Instant;
import java.util.UUID;

public record StockEvent(
        UUID eventId,
        Long productId,
        UUID farmerId,
        String cropName,
        int previousQuantity,
        int currentQuantity,
        int threshold,
        String eventType,
        Instant occurredAt) {

    public static final String TYPE_LOW_STOCK = "LOW_STOCK";
    public static final String TYPE_RESTOCKED = "RESTOCKED";
}