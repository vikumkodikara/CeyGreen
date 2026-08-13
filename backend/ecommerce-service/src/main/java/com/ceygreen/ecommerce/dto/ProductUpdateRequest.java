package com.ceygreen.ecommerce.dto;

import java.math.BigDecimal;

public record ProductUpdateRequest(
        BigDecimal unitPrice,
        Integer quantity,
        Boolean active,
        String description,
        String imageUrl,
        String location) {}
