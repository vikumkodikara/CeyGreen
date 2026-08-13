package com.ceygreen.ecommerce.repository;

import com.ceygreen.ecommerce.entity.Order;
import com.ceygreen.ecommerce.entity.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByBuyerId(UUID buyerId, Pageable pageable);

    Page<Order> findByBuyerIdAndStatus(UUID buyerId, OrderStatus status, Pageable pageable);

    Page<Order> findByFarmerId(UUID farmerId, Pageable pageable);

    Page<Order> findByFarmerIdAndStatus(UUID farmerId, OrderStatus status, Pageable pageable);

    long countByFarmerId(UUID farmerId);

    long countByFarmerIdAndStatus(UUID farmerId, OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.farmerId = :farmerId AND o.status = :status")
    BigDecimal sumTotalPriceByFarmerIdAndStatus(@Param("farmerId") UUID farmerId, @Param("status") OrderStatus status);

    List<Order> findByFarmerId(UUID farmerId);
}
