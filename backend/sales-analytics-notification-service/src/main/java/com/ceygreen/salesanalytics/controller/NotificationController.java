package com.ceygreen.salesanalytics.controller;

import com.ceygreen.salesanalytics.dto.NotificationDto;
import com.ceygreen.salesanalytics.dto.NotificationPreferenceRequestDto;
import com.ceygreen.salesanalytics.dto.NotificationPreferenceResponseDto;
import com.ceygreen.salesanalytics.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notify")
@Tag(name = "Notification System", description = "Endpoints for viewing notification history, managing preferences, and deleting entries")
@SecurityRequirement(name = "ApiKeyAuth")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/history/{userId}")
    @Operation(summary = "Get User Notification History", description = "Retrieves all delivered and outgoing notifications for a user ordered by timestamp")
    public ResponseEntity<List<NotificationDto>> getHistory(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getHistoryByUserId(userId));
    }

    @PutMapping("/preferences/{userId}")
    @Operation(summary = "Update User Notification Preference", description = "Sets or updates channel and event-level subscription preferences for a user")
    public ResponseEntity<NotificationPreferenceResponseDto> updatePreferences(
            @PathVariable String userId,
            @Valid @RequestBody NotificationPreferenceRequestDto request) {
        return ResponseEntity.ok(notificationService.updatePreferences(userId, request));
    }

    @DeleteMapping("/history/{id}")
    @Operation(summary = "Delete Notification History Entry", description = "Deletes a specific notification log entry by ID")
    public ResponseEntity<Map<String, Object>> deleteHistory(@PathVariable Long id) {
        notificationService.deleteHistoryById(id);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Notification history record deleted successfully",
                "id", id
        ));
    }
}
