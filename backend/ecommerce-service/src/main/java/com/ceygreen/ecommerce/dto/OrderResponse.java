package com.ceygreen.ecommerce.dto;

import com.ceygreen.ecommerce.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(
        Long id,
        UUID buyerId,
        Long productId,
        UUID farmerId,
        String cropName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice,
        OrderStatus status,
        Instant orderedAt,
        String buyerName,
        String phone,
        String address,
        String city,
        String postalCode) {}
