package com.ceygreen.notification.repository;

import com.ceygreen.notification.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByUserIdOrderByCreatedAtDesc(String userId);
}
