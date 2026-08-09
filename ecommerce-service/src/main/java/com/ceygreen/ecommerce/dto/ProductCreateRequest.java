package com.ceygreen.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ProductCreateRequest(
        @NotBlank String cropName,
        @Positive int quantity,
        @NotNull @DecimalMin("0.01") BigDecimal unitPrice,
        @NotNull LocalDate harvestDate,
        @NotBlank String location) {}
