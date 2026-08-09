package com.ceygreen.ecommerce.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

public record ProductUpdateRequest(
        @DecimalMin("0.01") BigDecimal unitPrice,
        @Min(0) Integer quantity,
        Boolean active) {}
