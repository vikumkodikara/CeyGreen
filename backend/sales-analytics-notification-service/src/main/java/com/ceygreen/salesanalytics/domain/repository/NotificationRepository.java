package com.ceygreen.salesanalytics.domain.repository;

import com.ceygreen.salesanalytics.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderBySentAtDesc(String userId);

    List<Notification> findByUserIdAndStatusOrderBySentAtDesc(String userId, String status);

    long countByUserId(String userId);
}
