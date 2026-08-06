package com.ceygreen.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CheckoutRequest(
        @NotBlank String buyerId,
        @NotEmpty List<CheckoutItem> items) {

    public record CheckoutItem(Long productId, int quantity) {}
}
