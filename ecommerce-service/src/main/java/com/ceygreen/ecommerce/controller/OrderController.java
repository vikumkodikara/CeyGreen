package com.ceygreen.ecommerce.controller;

import com.ceygreen.ecommerce.dto.CheckoutRequest;
import com.ceygreen.ecommerce.dto.OrderResponse;
import com.ceygreen.ecommerce.security.RequestIdentity;
import com.ceygreen.ecommerce.security.UserRole;
import com.ceygreen.ecommerce.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            HttpServletRequest request,
            @Valid @RequestBody CheckoutRequest body) {
        RequestIdentity.requireRole(request, UserRole.BUYER);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.checkout(RequestIdentity.requireBuyerId(request), body));
    }
}