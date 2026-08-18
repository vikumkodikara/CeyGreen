package com.ceygreen.salesanalytics.service;

import com.ceygreen.salesanalytics.domain.entity.OrderLog;
import com.ceygreen.salesanalytics.domain.entity.SalesSummary;
import com.ceygreen.salesanalytics.domain.repository.OrderLogRepository;
import com.ceygreen.salesanalytics.domain.repository.SalesSummaryRepository;
import com.ceygreen.salesanalytics.dto.LeaderboardEntryDto;
import com.ceygreen.salesanalytics.dto.OrderLogDto;
import com.ceygreen.salesanalytics.dto.SalesSummaryDto;
import com.ceygreen.salesanalytics.dto.SalesTrendDto;
import com.ceygreen.salesanalytics.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);

    private final SalesSummaryRepository salesSummaryRepository;
    private final OrderLogRepository orderLogRepository;

    public AnalyticsService(SalesSummaryRepository salesSummaryRepository,
                            OrderLogRepository orderLogRepository) {
        this.salesSummaryRepository = salesSummaryRepository;
        this.orderLogRepository = orderLogRepository;
    }

    public SalesSummaryDto getSalesSummary(String farmerId) {
        SalesSummary summary = salesSummaryRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Sales summary not found for farmer: " + farmerId));

        return mapToSummaryDto(summary);
    }

    public SalesTrendDto getSalesTrend(String farmerId) {
        SalesSummary summary = salesSummaryRepository.findById(farmerId)
                .orElseGet(() -> SalesSummary.builder()
                        .farmerId(farmerId)
                        .totalOrders(0L)
                        .totalRevenue(BigDecimal.ZERO)
                        .lastUpdated(LocalDateTime.now())
                        .build());

        List<OrderLog> logs = orderLogRepository.findTrendByFarmerId(farmerId);
        List<OrderLogDto> logDtos = logs.stream().map(this::mapToLogDto).collect(Collectors.toList());

        double aov = 0.0;
        if (summary.getTotalOrders() != null && summary.getTotalOrders() > 0 && summary.getTotalRevenue() != null) {
            aov = summary.getTotalRevenue()
                    .divide(BigDecimal.valueOf(summary.getTotalOrders()), 2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        return SalesTrendDto.builder()
                .farmerId(farmerId)
                .totalOrders(summary.getTotalOrders())
                .totalRevenue(summary.getTotalRevenue())
                .averageOrderValue(aov)
                .orderHistory(logDtos)
                .build();
    }

    public List<LeaderboardEntryDto> getLeaderboard() {
        List<SalesSummary> summaries = salesSummaryRepository.findAllOrderByTotalRevenueDesc();
        List<LeaderboardEntryDto> leaderboard = new ArrayList<>();

        int rank = 1;
        for (SalesSummary s : summaries) {
            leaderboard.add(LeaderboardEntryDto.builder()
                    .rank(rank++)
                    .farmerId(s.getFarmerId())
                    .totalOrders(s.getTotalOrders())
                    .totalRevenue(s.getTotalRevenue())
                    .lastUpdated(s.getLastUpdated())
                    .build());
        }

        return leaderboard;
    }

    public SalesSummaryDto mapToSummaryDto(SalesSummary summary) {
        return SalesSummaryDto.builder()
                .farmerId(summary.getFarmerId())
                .totalOrders(summary.getTotalOrders())
                .totalRevenue(summary.getTotalRevenue())
                .lastUpdated(summary.getLastUpdated())
                .build();
    }

    public OrderLogDto mapToLogDto(OrderLog log) {
        return OrderLogDto.builder()
                .id(log.getId())
                .farmerId(log.getFarmerId())
                .orderId(log.getOrderId())
                .amount(log.getAmount())
                .product(log.getProduct())
                .recordedAt(log.getRecordedAt())
                .build();
    }
}
