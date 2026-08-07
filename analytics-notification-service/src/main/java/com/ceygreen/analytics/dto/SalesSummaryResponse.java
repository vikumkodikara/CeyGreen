package com.ceygreen.analytics.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SalesSummaryResponse(String farmerId, BigDecimal totalRevenue, int totalOrders, Instant lastUpdated) {}
