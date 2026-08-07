package com.ceygreen.ecommerce.service;

import com.ceygreen.ecommerce.common.ApiException;
import com.ceygreen.ecommerce.dto.CheckoutRequest;
import com.ceygreen.ecommerce.dto.OrderResponse;
import com.ceygreen.ecommerce.kafka.OrderEventPublisher;
import com.ceygreen.ecommerce.kafka.StockEventPublisher;
import com.ceygreen.ecommerce.model.Order;
import com.ceygreen.ecommerce.model.OrderItem;
import com.ceygreen.ecommerce.model.Product;
import com.ceygreen.ecommerce.repository.OrderRepository;
import com.ceygreen.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final StockEventPublisher stockEventPublisher;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        OrderEventPublisher orderEventPublisher, StockEventPublisher stockEventPublisher) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderEventPublisher = orderEventPublisher;
        this.stockEventPublisher = stockEventPublisher;
    }

    @Transactional
    public OrderResponse checkout(CheckoutRequest request) {
        Order order = new Order();
        order.setBuyerId(request.buyerId());
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CheckoutRequest.CheckoutItem ci : request.items()) {
            Product product = productRepository.findById(ci.productId())
                    .orElseThrow(() -> ApiException.notFound("Product not found: " + ci.productId()));
            if (product.getQuantity() < ci.quantity()) {
                throw ApiException.badRequest("Insufficient stock for product: " + product.getName());
            }
            product.setQuantity(product.getQuantity() - ci.quantity());
            productRepository.save(product);

            OrderItem item = new OrderItem();
            item.setOrder(order); item.setProductId(product.getId());
            item.setProductName(product.getName()); item.setQuantity(ci.quantity());
            item.setUnitPrice(product.getPrice());
            items.add(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(ci.quantity())));

            if (product.getQuantity() <= 5) {
                stockEventPublisher.publishStockLow(product.getId(), product.getName(), product.getQuantity());
            }
        }

        order.setItems(items);
        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        orderEventPublisher.publishOrderPlaced(saved.getId(), saved.getBuyerId(), saved.getTotalAmount().toString());
        return new OrderResponse(saved.getId(), saved.getBuyerId(), saved.getTotalAmount(),
                saved.getStatus(), saved.getCreatedAt());
    }
}
