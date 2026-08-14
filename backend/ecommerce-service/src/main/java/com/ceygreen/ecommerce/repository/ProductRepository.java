package com.ceygreen.ecommerce.repository;

import com.ceygreen.ecommerce.entity.Order;
import com.ceygreen.ecommerce.entity.OrderStatus;
import com.ceygreen.ecommerce.entity.Product;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByActiveTrue();

    List<Product> findByActiveTrueAndCropNameIgnoreCase(String cropName);

    List<Product> findByActiveTrueAndLocationIgnoreCase(String location);

    List<Product> findByActiveTrueAndCropNameIgnoreCaseAndLocationIgnoreCase(String cropName, String location);

    List<Product> findByFarmerId(UUID farmerId);

    List<Product> findByFarmerIdAndQuantityLessThanEqualAndActiveTrue(UUID farmerId, int threshold);

    @Query("SELECT DISTINCT p.cropName FROM Product p WHERE p.active = true ORDER BY p.cropName")
    List<String> findDistinctActiveCropNames();

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.quantity > 0 ORDER BY p.harvestDate DESC")
    List<Product> findFeatured(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}
