package com.ceygreen.ecommerce.dto;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
        long totalProducts,
        long activeProducts,
        long inactiveProducts,
        long lowStockProducts,
        long totalOrders,
        long pendingOrders,
        long completedOrders,
        BigDecimal totalRevenue) {}
