package com.ceygreen.ecommerce.repository;

import com.ceygreen.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByAvailableTrue();
    List<Product> findByFarmerId(String farmerId);
}
