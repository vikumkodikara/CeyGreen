package com.ceygreen.analytics.service;

import com.ceygreen.analytics.common.ApiException;
import com.ceygreen.analytics.dto.LeaderboardResponse;
import com.ceygreen.analytics.dto.SalesSummaryResponse;
import com.ceygreen.analytics.dto.SalesTrendResponse;
import com.ceygreen.analytics.model.SalesSummary;
import com.ceygreen.analytics.repository.SalesSummaryRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AnalyticsService {
    private final SalesSummaryRepository salesSummaryRepository;

    public AnalyticsService(SalesSummaryRepository salesSummaryRepository) {
        this.salesSummaryRepository = salesSummaryRepository;
    }

    public SalesSummaryResponse getSalesSummary(String farmerId) {
        SalesSummary summary = salesSummaryRepository.findByFarmerId(farmerId)
                .orElseThrow(() -> ApiException.notFound("No sales data found for farmer: " + farmerId));
        return new SalesSummaryResponse(summary.getFarmerId(), summary.getTotalRevenue(),
                summary.getTotalOrders(), summary.getLastUpdated());
    }

    public SalesTrendResponse getSalesTrend(String farmerId) {
        // TODO: Implement trend calculation from order_log table
        return new SalesTrendResponse(farmerId, Collections.emptyList());
    }

    public LeaderboardResponse getLeaderboard() {
        AtomicInteger rank = new AtomicInteger(1);
        List<LeaderboardResponse.LeaderboardEntry> entries = salesSummaryRepository
                .findAllByOrderByTotalRevenueDesc().stream()
                .limit(10)
                .map(s -> new LeaderboardResponse.LeaderboardEntry(
                        s.getFarmerId(), s.getTotalRevenue(), s.getTotalOrders(), rank.getAndIncrement()))
                .toList();
        return new LeaderboardResponse(entries);
    }
}
