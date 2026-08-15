package com.ceygreen.analytics.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SalesTrendResponse(String farmerId, List<TrendPoint> trend) {
    public record TrendPoint(Instant date, BigDecimal revenue, int orders) {}
}
