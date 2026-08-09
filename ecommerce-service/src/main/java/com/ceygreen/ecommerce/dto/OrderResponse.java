package com.ceygreen.ecommerce.dto;

import com.ceygreen.ecommerce.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        Long id,
        UUID buyerId,
        Long productId,
        int quantity,
        BigDecimal totalPrice,
        OrderStatus status,
        Instant orderedAt) {}
