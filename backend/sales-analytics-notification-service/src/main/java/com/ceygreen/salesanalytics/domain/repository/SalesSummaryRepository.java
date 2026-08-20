package com.ceygreen.salesanalytics.domain.repository;

import com.ceygreen.salesanalytics.domain.entity.SalesSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesSummaryRepository extends JpaRepository<SalesSummary, String> {

    @Query("SELECT s FROM SalesSummary s ORDER BY s.totalRevenue DESC")
    List<SalesSummary> findAllOrderByTotalRevenueDesc();

    @Query("SELECT s FROM SalesSummary s ORDER BY s.totalOrders DESC")
    List<SalesSummary> findAllOrderByTotalOrdersDesc();
}
