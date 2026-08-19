package com.ceygreen.salesanalytics.service;

import com.ceygreen.salesanalytics.domain.entity.Notification;
import com.ceygreen.salesanalytics.domain.entity.NotificationPreference;
import com.ceygreen.salesanalytics.domain.entity.NotificationPreferenceId;
import com.ceygreen.salesanalytics.domain.repository.NotificationPreferenceRepository;
import com.ceygreen.salesanalytics.domain.repository.NotificationRepository;
import com.ceygreen.salesanalytics.dto.NotificationDto;
import com.ceygreen.salesanalytics.dto.NotificationPreferenceRequestDto;
import com.ceygreen.salesanalytics.dto.NotificationPreferenceResponseDto;
import com.ceygreen.salesanalytics.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationPreferenceRepository preferenceRepository) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getHistoryByUserId(String userId) {
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public NotificationPreferenceResponseDto updatePreferences(String userId, NotificationPreferenceRequestDto dto) {
        String eventType = dto.getEventType().trim();
        String channel = dto.getChannel().toUpperCase().trim();
        Boolean enabled = dto.getEnabled() != null ? dto.getEnabled() : true;

        NotificationPreferenceId id = new NotificationPreferenceId(userId, eventType, channel);
        NotificationPreference preference = preferenceRepository.findById(id)
                .orElse(NotificationPreference.builder()
                        .userId(userId)
                        .eventType(eventType)
                        .channel(channel)
                        .build());

        preference.setEnabled(enabled);
        preferenceRepository.save(preference);
        log.info("Updated notification preference for userId={}: eventType={}, channel={}, enabled={}",
                userId, eventType, channel, enabled);

        return getPreferencesByUserId(userId);
    }

    @Transactional(readOnly = true)
    public NotificationPreferenceResponseDto getPreferencesByUserId(String userId) {
        List<NotificationPreference> list = preferenceRepository.findByUserId(userId);
        List<NotificationPreferenceResponseDto.PreferenceEntryDto> entries = list.stream()
                .map(p -> NotificationPreferenceResponseDto.PreferenceEntryDto.builder()
                        .eventType(p.getEventType())
                        .channel(p.getChannel())
                        .enabled(p.getEnabled())
                        .build())
                .collect(Collectors.toList());

        return NotificationPreferenceResponseDto.builder()
                .userId(userId)
                .preferences(entries)
                .build();
    }

    public void deleteHistoryById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification record not found with ID: " + id));
        notificationRepository.delete(notification);
        log.info("Deleted notification record with ID: {}", id);
    }

    public void dispatchAndSaveNotification(String userId, String sourceTopic, String defaultChannel, String message) {
        if (userId == null || userId.isBlank()) {
            userId = "SYSTEM_BROADCAST";
        }

        // Check if user has opted out of this event topic
        Optional<NotificationPreference> optPref = preferenceRepository.findByUserIdAndEventType(userId, sourceTopic);
        if (optPref.isPresent() && Boolean.FALSE.equals(optPref.get().getEnabled())) {
            log.info("Notification suppressed for user {} due to user preference for topic: {}", userId, sourceTopic);
            return;
        }

        String targetChannel = optPref.map(NotificationPreference::getChannel).orElse(defaultChannel);

        Notification notification = Notification.builder()
                .userId(userId)
                .sourceTopic(sourceTopic)
                .channel(targetChannel != null ? targetChannel : "IN_APP")
                .message(message)
                .sentAt(LocalDateTime.now())
                .status("DELIVERED")
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("[NOTIFICATION DISPATCH] ID={} | User={} | Channel={} | Topic={} | Message: {}",
                saved.getId(), saved.getUserId(), saved.getChannel(), saved.getSourceTopic(), saved.getMessage());
    }

    public NotificationDto mapToDto(Notification entity) {
        return NotificationDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .sourceTopic(entity.getSourceTopic())
                .channel(entity.getChannel())
                .message(entity.getMessage())
                .sentAt(entity.getSentAt())
                .status(entity.getStatus())
                .build();
    }
}
