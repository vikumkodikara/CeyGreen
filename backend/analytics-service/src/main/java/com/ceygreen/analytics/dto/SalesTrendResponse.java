package com.ceygreen.analytics.dto;

import java.math.BigDecimal;
import java.util.List;

public record SalesTrendResponse(
        String farmerId,
        int totalOrders,
        BigDecimal totalRevenue,
        Double averageOrderValue,
        List<OrderLogDto> orderHistory,
        List<TrendPoint> trend
) {
    public record TrendPoint(String date, BigDecimal revenue, int orders) {}
}
