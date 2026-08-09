package com.ceygreen.ecommerce.dto;

import com.ceygreen.ecommerce.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderEvent(
        UUID eventId,
        Long orderId,
        UUID buyerId,
        UUID farmerId,
        Long productId,
        String cropName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        OrderStatus status,
        Instant orderedAt,
        String eventType) {}