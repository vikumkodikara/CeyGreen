package com.ceygreen.ecommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(Long id, String buyerId, BigDecimal totalAmount, String status, Instant createdAt) {}
