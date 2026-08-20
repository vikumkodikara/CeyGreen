package com.ceygreen.ecommerce.controller;

import com.ceygreen.ecommerce.dto.CheckoutRequest;
import com.ceygreen.ecommerce.dto.CheckoutResponse;
import com.ceygreen.ecommerce.dto.OrderResponse;
import com.ceygreen.ecommerce.dto.OrderStatusUpdateRequest;
import com.ceygreen.ecommerce.entity.OrderStatus;
import com.ceygreen.ecommerce.security.RequestIdentity;
import com.ceygreen.ecommerce.security.UserRole;
import com.ceygreen.ecommerce.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders")
@SecurityRequirement(name = "apiKey")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(
            HttpServletRequest request,
            @Valid @RequestBody CheckoutRequest body) {
        RequestIdentity.requireRole(request, UserRole.BUYER);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.checkout(RequestIdentity.requireBuyerId(request), body));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<Page<OrderResponse>> myOrders(
            HttpServletRequest request,
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable) {
        RequestIdentity.requireRole(request, UserRole.BUYER);
        UUID buyerId = RequestIdentity.requireBuyerId(request);
        return ResponseEntity.ok(orderService.getMyOrders(buyerId, status, pageable));
    }

    @GetMapping("/farmer")
    public ResponseEntity<Page<OrderResponse>> farmerOrders(
            HttpServletRequest request,
            @RequestParam(required = false) OrderStatus status,
            Pageable pageable) {
        RequestIdentity.requireRole(request, UserRole.FARMER);
        UUID farmerId = RequestIdentity.requireFarmerId(request);
        return ResponseEntity.ok(orderService.getFarmerOrders(farmerId, status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(HttpServletRequest request, @PathVariable Long id) {
        UserRole role = RequestIdentity.requireRole(request, UserRole.BUYER, UserRole.FARMER, UserRole.ADMIN);
        UUID buyerId = RequestIdentity.parseBuyerId(request).orElse(null);
        UUID farmerId = RequestIdentity.parseFarmerId(request).orElse(null);
        return ResponseEntity.ok(orderService.getOrder(id, buyerId, farmerId, role));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest body) {
        UserRole role = RequestIdentity.requireRole(request, UserRole.FARMER, UserRole.ADMIN);
        UUID farmerId = role == UserRole.ADMIN ? null : RequestIdentity.requireFarmerId(request);
        return ResponseEntity.ok(orderService.updateStatus(id, farmerId, role, body));
    }
}
