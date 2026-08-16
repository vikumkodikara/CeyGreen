package com.ceygreen.analytics.repository;

import com.ceygreen.analytics.model.SalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SalesSummaryRepository extends JpaRepository<SalesSummary, Long> {
    Optional<SalesSummary> findByFarmerId(String farmerId);
    List<SalesSummary> findAllByOrderByTotalRevenueDesc();
}
