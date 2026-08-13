package com.ceygreen.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductCreateRequest(
        @NotBlank String cropName,
        @Positive int quantity,
        @NotNull @Positive BigDecimal unitPrice,
        @NotNull LocalDate harvestDate,
        @NotBlank String location,
        String description,
        String imageUrl) {}
