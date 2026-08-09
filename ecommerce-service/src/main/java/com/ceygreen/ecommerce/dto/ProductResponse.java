package com.ceygreen.ecommerce.dto;

import java.math.BigDecimal;
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
        boolean active) {}
