package com.ceygreen.ecommerce.repository;

import com.ceygreen.ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
