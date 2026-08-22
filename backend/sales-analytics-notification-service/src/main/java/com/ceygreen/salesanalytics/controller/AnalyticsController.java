package com.ceygreen.salesanalytics.controller;

import com.ceygreen.salesanalytics.dto.LeaderboardEntryDto;
import com.ceygreen.salesanalytics.dto.SalesSummaryDto;
import com.ceygreen.salesanalytics.dto.SalesTrendDto;
import com.ceygreen.salesanalytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@Tag(name = "Analytics & Sales Intelligence", description = "Endpoints for farmer sales aggregates, trends, and leaderboard rankings")
@SecurityRequirement(name = "ApiKeyAuth")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * Retrieves the sales summary for a specific farmer by ID.
     *
     * @param farmerId The unique identifier of the farmer
     * @return SalesSummaryDto containing total revenue, total orders, and last updated time
     */
    @GetMapping("/sales/{farmerId}")
    @Operation(summary = "Get Farmer Sales Summary", description = "Retrieves total orders, total revenue, and last updated timestamp for a specific farmer")
    public ResponseEntity<SalesSummaryDto> getSalesSummary(@PathVariable String farmerId) {
        return ResponseEntity.ok(analyticsService.getSalesSummary(farmerId));
    }

    /**
     * Retrieves sales trend and order history for a specific farmer.
     *
     * @param farmerId The unique identifier of the farmer
     * @return SalesTrendDto containing trend metrics and order history logs
     */
    @GetMapping("/sales/{farmerId}/trend")
    @Operation(summary = "Get Farmer Sales Trend", description = "Retrieves sales volume, revenue, average order value, and historical order breakdown for a specific farmer")
    public ResponseEntity<SalesTrendDto> getSalesTrend(@PathVariable String farmerId) {
        return ResponseEntity.ok(analyticsService.getSalesTrend(farmerId));
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Get Farmer Sales Leaderboard", description = "Retrieves top farmers ranked by total sales revenue")
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard() {
        return ResponseEntity.ok(analyticsService.getLeaderboard());
    }
}
