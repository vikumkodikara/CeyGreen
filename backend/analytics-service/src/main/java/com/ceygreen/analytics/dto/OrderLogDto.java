package com.ceygreen.analytics.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderLogDto(
        Long id,
        String farmerId,
        String orderId,
        BigDecimal amount,
        String product,
        Integer quantity,
        BigDecimal unitPrice,
        Instant recordedAt
) {}
