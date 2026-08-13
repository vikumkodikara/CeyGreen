package com.ceygreen.ecommerce.dto;

import java.util.List;

public record CheckoutResponse(List<OrderResponse> orders) {}
