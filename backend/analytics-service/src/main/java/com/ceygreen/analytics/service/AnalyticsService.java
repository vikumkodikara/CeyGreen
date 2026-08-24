package com.ceygreen.analytics.service;

import com.ceygreen.analytics.dto.LeaderboardEntryDto;
import com.ceygreen.analytics.dto.OrderLogDto;
import com.ceygreen.analytics.dto.SalesSummaryResponse;
import com.ceygreen.analytics.dto.SalesTrendResponse;
import com.ceygreen.analytics.model.OrderLog;
import com.ceygreen.analytics.model.SalesSummary;
import com.ceygreen.analytics.repository.OrderLogRepository;
import com.ceygreen.analytics.repository.SalesSummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnalyticsService {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);

    private final SalesSummaryRepository salesSummaryRepository;
    private final OrderLogRepository orderLogRepository;

    public AnalyticsService(SalesSummaryRepository salesSummaryRepository,
                            OrderLogRepository orderLogRepository) {
        this.salesSummaryRepository = salesSummaryRepository;
        this.orderLogRepository = orderLogRepository;
    }

    public void processOrderEvent(Map<String, Object> payload) {
        if (payload == null) {
            return;
        }

        String farmerId = payload.get("farmerId") != null ? String.valueOf(payload.get("farmerId")) : null;
        if (farmerId == null || farmerId.isBlank()) {
            log.warn("Skipping order event without farmerId: {}", payload);
            return;
        }

        Long orderId = null;
        if (payload.get("orderId") != null) {
            try {
                orderId = Long.valueOf(String.valueOf(payload.get("orderId")));
            } catch (Exception ignored) {}
        }

        String buyerId = payload.get("buyerId") != null ? String.valueOf(payload.get("buyerId")) : null;
        Long productId = null;
        if (payload.get("productId") != null) {
            try {
                productId = Long.valueOf(String.valueOf(payload.get("productId")));
            } catch (Exception ignored) {}
        }

        String cropName = payload.get("cropName") != null
                ? String.valueOf(payload.get("cropName"))
                : (payload.get("product") != null ? String.valueOf(payload.get("product")) : "Produce");

        Integer quantity = 1;
        if (payload.get("quantity") != null) {
            try {
                quantity = Integer.valueOf(String.valueOf(payload.get("quantity")));
            } catch (Exception ignored) {}
        }

        BigDecimal unitPrice = BigDecimal.ZERO;
        if (payload.get("unitPrice") != null) {
            try {
                unitPrice = new BigDecimal(String.valueOf(payload.get("unitPrice")));
            } catch (Exception ignored) {}
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        if (payload.get("totalPrice") != null) {
            try {
                totalAmount = new BigDecimal(String.valueOf(payload.get("totalPrice")));
            } catch (Exception ignored) {}
        } else if (payload.get("amount") != null) {
            try {
                totalAmount = new BigDecimal(String.valueOf(payload.get("amount")));
            } catch (Exception ignored) {}
        }

        Instant receivedAt = Instant.now();
        if (payload.get("orderedAt") != null) {
            try {
                receivedAt = Instant.parse(String.valueOf(payload.get("orderedAt")));
            } catch (Exception ignored) {}
        }

        // 1. Record in OrderLog table
        OrderLog orderLog = new OrderLog(orderId, farmerId, buyerId, productId, cropName, quantity, unitPrice, totalAmount, receivedAt);
        orderLogRepository.save(orderLog);

        // 2. Update or create SalesSummary aggregate
        SalesSummary summary = salesSummaryRepository.findByFarmerId(farmerId)
                .orElseGet(() -> new SalesSummary(farmerId, BigDecimal.ZERO, 0, Instant.now()));

        summary.setTotalOrders(summary.getTotalOrders() + 1);
        summary.setTotalRevenue(summary.getTotalRevenue().add(totalAmount));
        summary.setLastUpdated(Instant.now());
        salesSummaryRepository.save(summary);

        log.info("Processed order event: farmerId={}, orderId={}, amount={}, newRevenue={}, newOrders={}",
                farmerId, orderId, totalAmount, summary.getTotalRevenue(), summary.getTotalOrders());
    }

    @Transactional(readOnly = true)
    public SalesSummaryResponse getSalesSummary(String farmerId) {
        return salesSummaryRepository.findByFarmerId(farmerId)
                .map(s -> new SalesSummaryResponse(s.getFarmerId(), s.getTotalRevenue(), s.getTotalOrders(), s.getLastUpdated()))
                .orElseGet(() -> new SalesSummaryResponse(farmerId, BigDecimal.ZERO, 0, Instant.now()));
    }

    @Transactional(readOnly = true)
    public SalesTrendResponse getSalesTrend(String farmerId) {
        SalesSummary summary = salesSummaryRepository.findByFarmerId(farmerId)
                .orElseGet(() -> new SalesSummary(farmerId, BigDecimal.ZERO, 0, Instant.now()));

        List<OrderLog> logs = orderLogRepository.findByFarmerIdOrderByReceivedAtDesc(farmerId);

        List<OrderLogDto> orderHistory = logs.stream()
                .map(l -> new OrderLogDto(
                        l.getId(),
                        l.getFarmerId(),
                        l.getOrderId() != null ? String.valueOf(l.getOrderId()) : String.valueOf(l.getId()),
                        l.getTotalAmount(),
                        l.getCropName(),
                        l.getQuantity(),
                        l.getUnitPrice(),
                        l.getReceivedAt()
                ))
                .collect(Collectors.toList());

        Double averageOrderValue = 0.0;
        if (summary.getTotalOrders() > 0 && summary.getTotalRevenue() != null) {
            averageOrderValue = summary.getTotalRevenue()
                    .divide(BigDecimal.valueOf(summary.getTotalOrders()), 2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        // Aggregate daily trend points
        Map<String, List<OrderLog>> byDate = logs.stream()
                .filter(l -> l.getReceivedAt() != null)
                .collect(Collectors.groupingBy(l -> DATE_FORMATTER.format(l.getReceivedAt()), LinkedHashMap::new, Collectors.toList()));

        List<SalesTrendResponse.TrendPoint> trendPoints = byDate.entrySet().stream()
                .map(e -> {
                    String date = e.getKey();
                    BigDecimal dailyRevenue = e.getValue().stream()
                            .map(OrderLog::getTotalAmount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    int dailyOrders = e.getValue().size();
                    return new SalesTrendResponse.TrendPoint(date, dailyRevenue, dailyOrders);
                })
                .collect(Collectors.toList());

        return new SalesTrendResponse(
                farmerId,
                summary.getTotalOrders(),
                summary.getTotalRevenue(),
                averageOrderValue,
                orderHistory,
                trendPoints
        );
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getLeaderboard() {
        List<SalesSummary> summaries = salesSummaryRepository.findAllByOrderByTotalRevenueDesc();
        List<LeaderboardEntryDto> leaderboard = new ArrayList<>();

        int rank = 1;
        for (SalesSummary s : summaries) {
            leaderboard.add(new LeaderboardEntryDto(
                    rank++,
                    s.getFarmerId(),
                    s.getTotalRevenue(),
                    s.getTotalOrders(),
                    s.getLastUpdated()
            ));
        }

        return leaderboard;
    }
}
