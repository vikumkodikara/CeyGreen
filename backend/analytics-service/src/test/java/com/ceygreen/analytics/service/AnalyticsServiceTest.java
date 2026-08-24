package com.ceygreen.analytics.service;

import com.ceygreen.analytics.dto.LeaderboardEntryDto;
import com.ceygreen.analytics.dto.SalesSummaryResponse;
import com.ceygreen.analytics.dto.SalesTrendResponse;
import com.ceygreen.analytics.model.OrderLog;
import com.ceygreen.analytics.model.SalesSummary;
import com.ceygreen.analytics.repository.OrderLogRepository;
import com.ceygreen.analytics.repository.SalesSummaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private SalesSummaryRepository salesSummaryRepository;

    @Mock
    private OrderLogRepository orderLogRepository;

    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        analyticsService = new AnalyticsService(salesSummaryRepository, orderLogRepository);
    }

    @Test
    void processOrderEvent_createsNewSummaryAndLog_whenFarmerFirstOrder() {
        String farmerId = "FARMER-101";
        when(salesSummaryRepository.findByFarmerId(farmerId)).thenReturn(Optional.empty());

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("orderId", 123L);
        payload.put("farmerId", farmerId);
        payload.put("buyerId", "BUYER-999");
        payload.put("productId", 45L);
        payload.put("cropName", "Strawberry");
        payload.put("quantity", 3);
        payload.put("unitPrice", "450.00");
        payload.put("totalPrice", "1350.00");
        payload.put("orderedAt", "2026-08-24T07:15:00Z");

        analyticsService.processOrderEvent(payload);

        verify(orderLogRepository).save(any(OrderLog.class));

        ArgumentCaptor<SalesSummary> summaryCaptor = ArgumentCaptor.forClass(SalesSummary.class);
        verify(salesSummaryRepository).save(summaryCaptor.capture());

        SalesSummary saved = summaryCaptor.getValue();
        assertThat(saved.getFarmerId()).isEqualTo(farmerId);
        assertThat(saved.getTotalOrders()).isEqualTo(1);
        assertThat(saved.getTotalRevenue()).isEqualByComparingTo(new BigDecimal("1350.00"));
    }

    @Test
    void processOrderEvent_aggregatesRevenueAndOrders_whenFarmerHasExistingSummary() {
        String farmerId = "FARMER-101";
        SalesSummary existing = new SalesSummary(farmerId, new BigDecimal("2000.00"), 2, Instant.now());
        when(salesSummaryRepository.findByFarmerId(farmerId)).thenReturn(Optional.of(existing));

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("orderId", 124L);
        payload.put("farmerId", farmerId);
        payload.put("cropName", "Tomato");
        payload.put("quantity", 5);
        payload.put("totalPrice", "500.00");

        analyticsService.processOrderEvent(payload);

        verify(salesSummaryRepository).save(existing);
        assertThat(existing.getTotalOrders()).isEqualTo(3);
        assertThat(existing.getTotalRevenue()).isEqualByComparingTo(new BigDecimal("2500.00"));
    }

    @Test
    void getSalesSummary_returnsSummary_whenFound() {
        String farmerId = "FARMER-101";
        SalesSummary summary = new SalesSummary(farmerId, new BigDecimal("5000.00"), 4, Instant.now());
        when(salesSummaryRepository.findByFarmerId(farmerId)).thenReturn(Optional.of(summary));

        SalesSummaryResponse response = analyticsService.getSalesSummary(farmerId);

        assertThat(response.farmerId()).isEqualTo(farmerId);
        assertThat(response.totalRevenue()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(response.totalOrders()).isEqualTo(4);
    }

    @Test
    void getSalesSummary_returnsDefaultZero_whenNotFound() {
        String farmerId = "UNKNOWN";
        when(salesSummaryRepository.findByFarmerId(farmerId)).thenReturn(Optional.empty());

        SalesSummaryResponse response = analyticsService.getSalesSummary(farmerId);

        assertThat(response.farmerId()).isEqualTo(farmerId);
        assertThat(response.totalRevenue()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.totalOrders()).isEqualTo(0);
    }

    @Test
    void getSalesTrend_calculatesAovAndHistoryAndDailyTrend() {
        String farmerId = "FARMER-101";
        SalesSummary summary = new SalesSummary(farmerId, new BigDecimal("3000.00"), 2, Instant.now());
        when(salesSummaryRepository.findByFarmerId(farmerId)).thenReturn(Optional.of(summary));

        OrderLog log1 = new OrderLog(101L, farmerId, "BUYER-1", 1L, "Strawberry", 2, new BigDecimal("500.00"), new BigDecimal("1000.00"), Instant.parse("2026-08-24T05:00:00Z"));
        OrderLog log2 = new OrderLog(102L, farmerId, "BUYER-2", 2L, "Strawberry", 4, new BigDecimal("500.00"), new BigDecimal("2000.00"), Instant.parse("2026-08-24T06:00:00Z"));
        when(orderLogRepository.findByFarmerIdOrderByReceivedAtDesc(farmerId)).thenReturn(List.of(log2, log1));

        SalesTrendResponse trend = analyticsService.getSalesTrend(farmerId);

        assertThat(trend.farmerId()).isEqualTo(farmerId);
        assertThat(trend.totalOrders()).isEqualTo(2);
        assertThat(trend.totalRevenue()).isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(trend.averageOrderValue()).isEqualTo(1500.0);
        assertThat(trend.orderHistory()).hasSize(2);
        assertThat(trend.trend()).hasSize(1);
        assertThat(trend.trend().get(0).revenue()).isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(trend.trend().get(0).orders()).isEqualTo(2);
    }

    @Test
    void getLeaderboard_returnsRankedList() {
        SalesSummary s1 = new SalesSummary("FARMER-A", new BigDecimal("10000.00"), 10, Instant.now());
        SalesSummary s2 = new SalesSummary("FARMER-B", new BigDecimal("5000.00"), 5, Instant.now());
        when(salesSummaryRepository.findAllByOrderByTotalRevenueDesc()).thenReturn(List.of(s1, s2));

        List<LeaderboardEntryDto> leaderboard = analyticsService.getLeaderboard();

        assertThat(leaderboard).hasSize(2);
        assertThat(leaderboard.get(0).rank()).isEqualTo(1);
        assertThat(leaderboard.get(0).farmerId()).isEqualTo("FARMER-A");
        assertThat(leaderboard.get(1).rank()).isEqualTo(2);
        assertThat(leaderboard.get(1).farmerId()).isEqualTo("FARMER-B");
    }
}
