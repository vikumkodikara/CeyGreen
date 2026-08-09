package com.ceygreen.ecommerce.repository;

import com.ceygreen.ecommerce.entity.Product;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByActiveTrue();

    List<Product> findByActiveTrueAndCropNameIgnoreCase(String cropName);

    List<Product> findByActiveTrueAndLocationIgnoreCase(String location);

    List<Product> findByActiveTrueAndCropNameIgnoreCaseAndLocationIgnoreCase(String cropName, String location);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}