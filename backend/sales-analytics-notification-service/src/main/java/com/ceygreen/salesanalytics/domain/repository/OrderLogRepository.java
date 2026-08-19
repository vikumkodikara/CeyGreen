package com.ceygreen.salesanalytics.domain.repository;

import com.ceygreen.salesanalytics.domain.entity.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {

    List<OrderLog> findByFarmerIdOrderByRecordedAtDesc(String farmerId);

    List<OrderLog> findByFarmerIdAndRecordedAtBetweenOrderByRecordedAtAsc(
            String farmerId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT o FROM OrderLog o WHERE o.farmerId = :farmerId ORDER BY o.recordedAt ASC")
    List<OrderLog> findTrendByFarmerId(@Param("farmerId") String farmerId);
}
