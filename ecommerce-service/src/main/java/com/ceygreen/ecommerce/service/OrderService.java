package com.ceygreen.ecommerce.service;

import com.ceygreen.ecommerce.common.ApiException;
import com.ceygreen.ecommerce.dto.CheckoutRequest;
import com.ceygreen.ecommerce.dto.OrderEvent;
import com.ceygreen.ecommerce.dto.OrderResponse;
import com.ceygreen.ecommerce.entity.Order;
import com.ceygreen.ecommerce.entity.OrderStatus;
import com.ceygreen.ecommerce.entity.Product;
import com.ceygreen.ecommerce.kafka.OrderEventPublisher;
import com.ceygreen.ecommerce.repository.OrderRepository;
import com.ceygreen.ecommerce.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.UUID;
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
    public OrderResponse checkout(UUID buyerId, CheckoutRequest request) {
        Product product = productRepository.findByIdForUpdate(request.productId())
                .orElseThrow(() -> ApiException.notFound("Product not found: " + request.productId()));

        if (!product.isActive()) {
            throw ApiException.badRequest("Product is not available for purchase");
        }
        if (product.getQuantity() < request.quantity()) {
            throw ApiException.badRequest("Insufficient stock for product: " + product.getCropName());
        }

        int previousQuantity = product.getQuantity();
        int newQuantity = previousQuantity - request.quantity();
        product.setQuantity(newQuantity);
        Product savedProduct = productRepository.save(product);

        BigDecimal totalPrice = savedProduct.getUnitPrice().multiply(BigDecimal.valueOf(request.quantity()));

        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setProductId(savedProduct.getId());
        order.setQuantity(request.quantity());
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.COMPLETED);
        Order saved = orderRepository.save(order);

        stockEventService.evaluateQuantityChange(savedProduct, previousQuantity, newQuantity);

        OrderEvent event = new OrderEvent(
                UUID.randomUUID(),
                saved.getId(),
                saved.getBuyerId(),
                savedProduct.getFarmerId(),
                savedProduct.getId(),
                savedProduct.getCropName(),
                saved.getQuantity(),
                savedProduct.getUnitPrice(),
                saved.getTotalPrice(),
                saved.getStatus(),
                saved.getOrderedAt(),
                "ORDER_CREATED");
        orderEventPublisher.publishOrderCreated(event);

        return toResponse(saved);
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getBuyerId(),
                order.getProductId(),
                order.getQuantity(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getOrderedAt());
    }
}