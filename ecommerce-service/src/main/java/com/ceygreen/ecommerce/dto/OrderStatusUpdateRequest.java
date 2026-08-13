package com.ceygreen.ecommerce.dto;

import com.ceygreen.ecommerce.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(@NotNull OrderStatus status) {}
