package com.ceygreen.ecommerce.service;

import com.ceygreen.ecommerce.dto.OrderResponse;
import com.ceygreen.ecommerce.entity.Order;

public final class OrderMapper {

    private OrderMapper() {}

    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getBuyerId(),
                order.getProductId(),
                order.getFarmerId(),
                order.getCropName(),
                order.getQuantity(),
                order.getUnitPrice(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getOrderedAt(),
                order.getBuyerName(),
                order.getPhone(),
                order.getAddress(),
                order.getCity(),
                order.getPostalCode());
    }
}
