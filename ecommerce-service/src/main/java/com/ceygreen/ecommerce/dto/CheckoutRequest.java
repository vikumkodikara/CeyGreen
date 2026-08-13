package com.ceygreen.ecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record CheckoutRequest(
        @Valid List<CheckoutItem> items,
        Long productId,
        @Positive Integer quantity,
        @NotBlank String buyerName,
        @NotBlank String phone,
        @NotBlank String address,
        @NotBlank String city,
        @NotBlank String postalCode) {

    public record CheckoutItem(
            @NotNull Long productId,
            @Positive int quantity) {}

    public List<CheckoutItem> resolvedItems() {
        if (items != null && !items.isEmpty()) {
            return items;
        }
        if (productId != null && quantity != null) {
            return List.of(new CheckoutItem(productId, quantity));
        }
        throw new IllegalArgumentException("Checkout requires items or productId and quantity");
    }
}
