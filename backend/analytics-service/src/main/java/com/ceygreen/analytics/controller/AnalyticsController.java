package com.ceygreen.analytics.controller;

import com.ceygreen.analytics.dto.LeaderboardResponse;
import com.ceygreen.analytics.dto.SalesSummaryResponse;
import com.ceygreen.analytics.dto.SalesTrendResponse;
import com.ceygreen.analytics.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/sales/{farmerId}")
    public ResponseEntity<SalesSummaryResponse> getSalesSummary(@PathVariable String farmerId) {
        return ResponseEntity.ok(analyticsService.getSalesSummary(farmerId));
    }

    @GetMapping("/sales/{farmerId}/trend")
    public ResponseEntity<SalesTrendResponse> getSalesTrend(@PathVariable String farmerId) {
        return ResponseEntity.ok(analyticsService.getSalesTrend(farmerId));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<LeaderboardResponse> getLeaderboard() {
        return ResponseEntity.ok(analyticsService.getLeaderboard());
    }
}
