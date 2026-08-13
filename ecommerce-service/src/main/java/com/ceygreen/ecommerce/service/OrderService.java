package com.ceygreen.ecommerce.service;

import com.ceygreen.ecommerce.common.ApiException;
import com.ceygreen.ecommerce.dto.CheckoutRequest;
import com.ceygreen.ecommerce.dto.CheckoutResponse;
import com.ceygreen.ecommerce.dto.OrderResponse;
import com.ceygreen.ecommerce.dto.OrderStatusUpdateRequest;
import com.ceygreen.ecommerce.entity.Order;
import com.ceygreen.ecommerce.entity.OrderStatus;
import com.ceygreen.ecommerce.entity.Product;
import com.ceygreen.ecommerce.kafka.OrderEventPublisher;
import com.ceygreen.ecommerce.repository.OrderRepository;
import com.ceygreen.ecommerce.repository.ProductRepository;
import com.ceygreen.ecommerce.security.UserRole;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final StockEventService stockEventService;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            OrderEventPublisher orderEventPublisher,
            StockEventService stockEventService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderEventPublisher = orderEventPublisher;
        this.stockEventService = stockEventService;
    }

    @Transactional
    public CheckoutResponse checkout(UUID buyerId, CheckoutRequest request) {
        List<CheckoutRequest.CheckoutItem> items;
        try {
            items = request.resolvedItems();
        } catch (IllegalArgumentException ex) {
            throw ApiException.badRequest(ex.getMessage());
        }
        if (items.isEmpty()) {
            throw ApiException.badRequest("Checkout requires at least one item");
        }

        List<OrderResponse> created = new ArrayList<>();
        for (CheckoutRequest.CheckoutItem item : items) {
            created.add(processCheckoutItem(buyerId, request, item));
        }
        return new CheckoutResponse(created);
    }

    private OrderResponse processCheckoutItem(
            UUID buyerId, CheckoutRequest request, CheckoutRequest.CheckoutItem item) {
        Product product = productRepository.findByIdForUpdate(item.productId())
                .orElseThrow(() -> ApiException.notFound("Product not found: " + item.productId()));

        if (!product.isActive()) {
            throw ApiException.badRequest("Product is not available for purchase: " + product.getCropName());
        }
        if (product.getQuantity() < item.quantity()) {
            throw ApiException.badRequest("Insufficient stock for product: " + product.getCropName());
        }

        int previousQuantity = product.getQuantity();
        int newQuantity = previousQuantity - item.quantity();
        product.setQuantity(newQuantity);
        Product savedProduct = productRepository.save(product);

        BigDecimal totalPrice = savedProduct.getUnitPrice().multiply(BigDecimal.valueOf(item.quantity()));

        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setProductId(savedProduct.getId());
        order.setFarmerId(savedProduct.getFarmerId());
        order.setCropName(savedProduct.getCropName());
        order.setUnitPrice(savedProduct.getUnitPrice());
        order.setQuantity(item.quantity());
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PENDING);
        order.setBuyerName(request.buyerName().trim());
        order.setPhone(request.phone().trim());
        order.setAddress(request.address().trim());
        order.setCity(request.city().trim());
        order.setPostalCode(request.postalCode().trim());
        Order saved = orderRepository.save(order);

        stockEventService.evaluateQuantityChange(savedProduct, previousQuantity, newQuantity);
        orderEventPublisher.publishOrderEvent(saved, "ORDER_CREATED");

        return OrderMapper.toResponse(saved);
    }

    public OrderResponse getOrder(Long id, UUID buyerId, UUID farmerId, UserRole role) {
        Order order = findOrder(id);
        assertCanView(order, buyerId, farmerId, role);
        return OrderMapper.toResponse(order);
    }

    public Page<OrderResponse> getMyOrders(UUID buyerId, OrderStatus status, Pageable pageable) {
        Page<Order> page = status == null
                ? orderRepository.findByBuyerId(buyerId, pageable)
                : orderRepository.findByBuyerIdAndStatus(buyerId, status, pageable);
        return page.map(OrderMapper::toResponse);
    }

    public Page<OrderResponse> getFarmerOrders(UUID farmerId, OrderStatus status, Pageable pageable) {
        Page<Order> page = status == null
                ? orderRepository.findByFarmerId(farmerId, pageable)
                : orderRepository.findByFarmerIdAndStatus(farmerId, status, pageable);
        return page.map(OrderMapper::toResponse);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, UUID farmerId, UserRole role, OrderStatusUpdateRequest request) {
        Order order = findOrder(id);
        if (role != UserRole.ADMIN && (order.getFarmerId() == null || !order.getFarmerId().equals(farmerId))) {
            throw ApiException.forbidden("You may only update orders for your own products");
        }
        if (!OrderStatusTransitions.isAllowed(order.getStatus(), request.status())) {
            throw ApiException.badRequest("Invalid status transition from " + order.getStatus() + " to " + request.status());
        }
        order.setStatus(request.status());
        Order saved = orderRepository.save(order);
        orderEventPublisher.publishOrderEvent(saved, "ORDER_STATUS_CHANGED");
        return OrderMapper.toResponse(saved);
    }

    public long countByFarmer(UUID farmerId) {
        return orderRepository.countByFarmerId(farmerId);
    }

    public long countPendingByFarmer(UUID farmerId) {
        return orderRepository.countByFarmerIdAndStatus(farmerId, OrderStatus.PENDING);
    }

    public long countDeliveredByFarmer(UUID farmerId) {
        return orderRepository.countByFarmerIdAndStatus(farmerId, OrderStatus.DELIVERED);
    }

    public BigDecimal revenueByFarmer(UUID farmerId) {
        return orderRepository.sumTotalPriceByFarmerIdAndStatus(farmerId, OrderStatus.DELIVERED);
    }

    private Order findOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Order not found: " + id));
    }

    private static void assertCanView(Order order, UUID buyerId, UUID farmerId, UserRole role) {
        if (role == UserRole.ADMIN) {
            return;
        }
        if (buyerId != null && order.getBuyerId().equals(buyerId)) {
            return;
        }
        if (farmerId != null && order.getFarmerId() != null && order.getFarmerId().equals(farmerId)) {
            return;
        }
        throw ApiException.forbidden("You may not view this order");
    }
}
