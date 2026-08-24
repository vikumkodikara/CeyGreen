package com.ceygreen.analytics.controller;

import com.ceygreen.analytics.dto.LeaderboardEntryDto;
import com.ceygreen.analytics.dto.SalesSummaryResponse;
import com.ceygreen.analytics.dto.SalesTrendResponse;
import com.ceygreen.analytics.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping({"/api/analytics", "/analytics", ""})
@Tag(name = "Analytics", description = "Endpoints for farmer sales aggregates, trends, and leaderboard rankings")
@SecurityRequirement(name = "apiKey")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/sales/{farmerId}")
    @Operation(summary = "Get Farmer Sales Summary", description = "Retrieves total orders, total revenue, and last updated timestamp for a specific farmer")
    public ResponseEntity<SalesSummaryResponse> getSalesSummary(@PathVariable String farmerId) {
        return ResponseEntity.ok(analyticsService.getSalesSummary(farmerId));
    }

    @GetMapping("/sales/{farmerId}/trend")
    @Operation(summary = "Get Farmer Sales Trend", description = "Retrieves sales volume, revenue, average order value, and historical order breakdown for a specific farmer")
    public ResponseEntity<SalesTrendResponse> getSalesTrend(@PathVariable String farmerId) {
        return ResponseEntity.ok(analyticsService.getSalesTrend(farmerId));
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Get Farmer Sales Leaderboard", description = "Retrieves top farmers ranked by total sales revenue")
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard() {
        return ResponseEntity.ok(analyticsService.getLeaderboard());
    }
}
