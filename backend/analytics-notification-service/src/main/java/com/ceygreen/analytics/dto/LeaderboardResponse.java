package com.ceygreen.analytics.dto;

import java.math.BigDecimal;
import java.util.List;

public record LeaderboardResponse(List<LeaderboardEntry> farmers) {
    public record LeaderboardEntry(String farmerId, BigDecimal totalRevenue, int totalOrders, int rank) {}
}
