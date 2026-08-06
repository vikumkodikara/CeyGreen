package com.ceygreen.ecommerce.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String name,
        String description,
        @NotBlank String farmerId,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @Min(0) int quantity,
        String cropType) {}
