package com.ceygreen.salesanalytics.domain.repository;

import com.ceygreen.salesanalytics.domain.entity.NotificationPreference;
import com.ceygreen.salesanalytics.domain.entity.NotificationPreferenceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, NotificationPreferenceId> {

    List<NotificationPreference> findByUserId(String userId);

    Optional<NotificationPreference> findByUserIdAndEventTypeAndChannel(
            String userId, String eventType, String channel);

    Optional<NotificationPreference> findByUserIdAndEventType(
            String userId, String eventType);

    void deleteByUserId(String userId);
}
