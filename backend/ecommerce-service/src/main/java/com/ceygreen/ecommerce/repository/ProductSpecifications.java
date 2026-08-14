package com.ceygreen.ecommerce.repository;

import com.ceygreen.ecommerce.entity.Product;
import com.ceygreen.ecommerce.entity.ProductListingStatus;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecifications {

    private ProductSpecifications() {}

    public static Specification<Product> withFilters(
            String q,
            String cropName,
            String location,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            Boolean active,
            UUID farmerId,
            ProductListingStatus statusFilter) {
        return Specification.allOf(
                searchQuery(q),
                cropNameEquals(cropName),
                locationEquals(location),
                minPriceAtLeast(minPrice),
                maxPriceAtMost(maxPrice),
                inStockOnly(inStock),
                activeEquals(active),
                farmerEquals(farmerId),
                statusEquals(statusFilter));
    }

    private static Specification<Product> searchQuery(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + q.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("cropName")), pattern),
                    cb.like(cb.lower(root.get("location")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern));
        };
    }

    private static Specification<Product> cropNameEquals(String cropName) {
        return (root, query, cb) -> {
            if (cropName == null || cropName.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("cropName")), cropName.trim().toLowerCase());
        };
    }

    private static Specification<Product> locationEquals(String location) {
        return (root, query, cb) -> {
            if (location == null || location.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get("location")), location.trim().toLowerCase());
        };
    }

    private static Specification<Product> minPriceAtLeast(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("unitPrice"), minPrice);
    }

    private static Specification<Product> maxPriceAtMost(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("unitPrice"), maxPrice);
    }

    private static Specification<Product> inStockOnly(Boolean inStock) {
        return (root, query, cb) -> {
            if (inStock == null || !inStock) {
                return cb.conjunction();
            }
            return cb.greaterThan(root.get("quantity"), 0);
        };
    }

    private static Specification<Product> activeEquals(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("active"), active);
        };
    }

    private static Specification<Product> farmerEquals(UUID farmerId) {
        return (root, query, cb) -> farmerId == null ? cb.conjunction() : cb.equal(root.get("farmerId"), farmerId);
    }

    private static Specification<Product> statusEquals(ProductListingStatus statusFilter) {
        return (root, query, cb) -> {
            if (statusFilter == null) {
                return cb.conjunction();
            }
            return switch (statusFilter) {
                case INACTIVE -> cb.isFalse(root.get("active"));
                case OUT_OF_STOCK -> cb.and(cb.isTrue(root.get("active")), cb.equal(root.get("quantity"), 0));
                case ACTIVE -> cb.and(cb.isTrue(root.get("active")), cb.greaterThan(root.get("quantity"), 0));
            };
        };
    }
}
