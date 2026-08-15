package com.ceygreen.notification.controller;

import com.ceygreen.notification.dto.NotificationPreferenceRequest;
import com.ceygreen.notification.model.NotificationLog;
import com.ceygreen.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notify")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<NotificationLog>> getHistory(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getNotificationHistory(userId));
    }

    @PutMapping("/preferences/{userId}")
    public ResponseEntity<Void> updatePreferences(@PathVariable String userId,
                                                    @RequestBody NotificationPreferenceRequest request) {
        notificationService.updatePreferences(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }
}
