package com.ceygreen.salesanalytics.controller;

import com.ceygreen.salesanalytics.dto.NotificationDto;
import com.ceygreen.salesanalytics.dto.NotificationPreferenceRequestDto;
import com.ceygreen.salesanalytics.dto.NotificationPreferenceResponseDto;
import com.ceygreen.salesanalytics.security.ApiKeyAuthenticationFilter;
import com.ceygreen.salesanalytics.security.SecurityConfig;
import com.ceygreen.salesanalytics.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@Import({SecurityConfig.class, ApiKeyAuthenticationFilter.class})
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String VALID_API_KEY = "ceygreen-secret-api-key-2026";

    @Test
    void getHistory_WithValidApiKey_ReturnsNotificationList() throws Exception {
        NotificationDto notif = NotificationDto.builder()
                .id(1L)
                .userId("USER-001")
                .sourceTopic("order-events")
                .channel("IN_APP")
                .message("Order confirmation received")
                .sentAt(LocalDateTime.now())
                .status("DELIVERED")
                .build();

        when(notificationService.getHistoryByUserId("USER-001")).thenReturn(List.of(notif));

        mockMvc.perform(get("/notify/history/USER-001")
                        .header(API_KEY_HEADER, VALID_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userId").value("USER-001"))
                .andExpect(jsonPath("$[0].sourceTopic").value("order-events"));
    }

    @Test
    void updatePreferences_WithValidApiKey_ReturnsUpdatedPreferences() throws Exception {
        NotificationPreferenceRequestDto request = NotificationPreferenceRequestDto.builder()
                .eventType("greenhouse-alerts")
                .channel("SMS")
                .enabled(true)
                .build();

        NotificationPreferenceResponseDto response = NotificationPreferenceResponseDto.builder()
                .userId("USER-001")
                .preferences(List.of(
                        NotificationPreferenceResponseDto.PreferenceEntryDto.builder()
                                .eventType("greenhouse-alerts")
                                .channel("SMS")
                                .enabled(true)
                                .build()
                ))
                .build();

        when(notificationService.updatePreferences(eq("USER-001"), any(NotificationPreferenceRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(put("/notify/preferences/USER-001")
                        .header(API_KEY_HEADER, VALID_API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("USER-001"))
                .andExpect(jsonPath("$.preferences[0].eventType").value("greenhouse-alerts"))
                .andExpect(jsonPath("$.preferences[0].channel").value("SMS"));
    }

    @Test
    void deleteHistory_WithValidApiKey_ReturnsOk() throws Exception {
        doNothing().when(notificationService).deleteHistoryById(10L);

        mockMvc.perform(delete("/notify/history/10")
                        .header(API_KEY_HEADER, VALID_API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.id").value(10));
    }
}
