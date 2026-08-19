package com.ceygreen.salesanalytics.controller;

import com.ceygreen.salesanalytics.dto.LeaderboardEntryDto;
import com.ceygreen.salesanalytics.dto.SalesSummaryDto;
import com.ceygreen.salesanalytics.dto.SalesTrendDto;
import com.ceygreen.salesanalytics.security.ApiKeyAuthenticationFilter;
import com.ceygreen.salesanalytics.security.SecurityConfig;
import com.ceygreen.salesanalytics.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsController.class)
@Import({SecurityConfig.class, ApiKeyAuthenticationFilter.class})
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String VALID_API_KEY = "ceygreen-secret-api-key-2026";

    @Test
    void getSalesSummary_WithoutApiKey_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/analytics/sales/FARMER-101"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getSalesSummary_WithValidApiKey_ReturnsSummary() throws Exception {
        SalesSummaryDto dto = SalesSummaryDto.builder()
                .farmerId("FARMER-101")
                .totalOrders(15L)
                .totalRevenue(BigDecimal.valueOf(185000.00))
                .lastUpdated(LocalDateTime.now())
                .build();

        when(analyticsService.getSalesSummary("FARMER-101")).thenReturn(dto);

        mockMvc.perform(get("/analytics/sales/FARMER-101")
                        .header(API_KEY_HEADER, VALID_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.farmerId").value("FARMER-101"))
                .andExpect(jsonPath("$.totalOrders").value(15))
                .andExpect(jsonPath("$.totalRevenue").value(185000.00));
    }

    @Test
    void getSalesTrend_WithValidApiKey_ReturnsTrend() throws Exception {
        SalesTrendDto trendDto = SalesTrendDto.builder()
                .farmerId("FARMER-101")
                .totalOrders(10L)
                .totalRevenue(BigDecimal.valueOf(120000.00))
                .averageOrderValue(12000.00)
                .orderHistory(Collections.emptyList())
                .build();

        when(analyticsService.getSalesTrend("FARMER-101")).thenReturn(trendDto);

        mockMvc.perform(get("/analytics/sales/FARMER-101/trend")
                        .header(API_KEY_HEADER, VALID_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.farmerId").value("FARMER-101"))
                .andExpect(jsonPath("$.averageOrderValue").value(12000.00));
    }

    @Test
    void getLeaderboard_WithValidApiKey_ReturnsRankedList() throws Exception {
        LeaderboardEntryDto entry = LeaderboardEntryDto.builder()
                .rank(1)
                .farmerId("FARMER-103")
                .totalOrders(25L)
                .totalRevenue(BigDecimal.valueOf(310000.00))
                .lastUpdated(LocalDateTime.now())
                .build();

        when(analyticsService.getLeaderboard()).thenReturn(List.of(entry));

        mockMvc.perform(get("/analytics/leaderboard")
                        .header(API_KEY_HEADER, VALID_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rank").value(1))
                .andExpect(jsonPath("$[0].farmerId").value("FARMER-103"))
                .andExpect(jsonPath("$[0].totalRevenue").value(310000.00));
    }
}
