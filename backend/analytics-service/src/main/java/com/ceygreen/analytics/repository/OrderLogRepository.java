package com.ceygreen.analytics.repository;

import com.ceygreen.analytics.model.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {
    List<OrderLog> findByFarmerIdOrderByReceivedAtDesc(String farmerId);
    List<OrderLog> findTop50ByFarmerIdOrderByReceivedAtDesc(String farmerId);
}
