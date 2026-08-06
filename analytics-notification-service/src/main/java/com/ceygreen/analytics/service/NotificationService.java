package com.ceygreen.analytics.service;

import com.ceygreen.analytics.common.ApiException;
import com.ceygreen.analytics.dto.NotificationPreferenceRequest;
import com.ceygreen.analytics.model.NotificationLog;
import com.ceygreen.analytics.model.NotificationPreference;
import com.ceygreen.analytics.repository.NotificationLogRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationLogRepository notificationLogRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public NotificationService(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    public List<NotificationLog> getNotificationHistory(String userId) {
        return notificationLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void updatePreferences(String userId, NotificationPreferenceRequest request) {
        // TODO: Implement preference upsert via NotificationPreferenceRepository
    }

    public void deleteNotification(Long id) {
        if (!notificationLogRepository.existsById(id)) {
            throw ApiException.notFound("Notification not found: " + id);
        }
        notificationLogRepository.deleteById(id);
    }
}
