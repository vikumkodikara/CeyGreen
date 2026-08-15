package com.ceygreen.ecommerce.dto;

import com.ceygreen.ecommerce.entity.ProductListingStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProductResponse(
        Long id,
        UUID farmerId,
        String cropName,
        int quantity,
        BigDecimal unitPrice,
        LocalDate harvestDate,
        String location,
        String description,
        String imageUrl,
        Instant createdAt,
        boolean active,
        ProductListingStatus status) {}
