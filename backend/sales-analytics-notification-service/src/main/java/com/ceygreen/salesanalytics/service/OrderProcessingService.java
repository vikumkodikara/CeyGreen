package com.ceygreen.salesanalytics.service;

import com.ceygreen.salesanalytics.domain.entity.OrderLog;
import com.ceygreen.salesanalytics.domain.entity.SalesSummary;
import com.ceygreen.salesanalytics.domain.repository.OrderLogRepository;
import com.ceygreen.salesanalytics.domain.repository.SalesSummaryRepository;
import com.ceygreen.salesanalytics.dto.event.OrderEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class OrderProcessingService {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessingService.class);

    private final OrderLogRepository orderLogRepository;
    private final SalesSummaryRepository salesSummaryRepository;
    private final NotificationService notificationService;

    public OrderProcessingService(OrderLogRepository orderLogRepository,
                                  SalesSummaryRepository salesSummaryRepository,
                                  NotificationService notificationService) {
        this.orderLogRepository = orderLogRepository;
        this.salesSummaryRepository = salesSummaryRepository;
        this.notificationService = notificationService;
    }

    public void processOrder(OrderEventDto orderEvent) {
        String farmerId = orderEvent.getFarmerId();
        String orderId = orderEvent.getOrderId();
        BigDecimal amount = orderEvent.getAmount() != null ? orderEvent.getAmount() : BigDecimal.ZERO;
        String product = orderEvent.getProduct() != null ? orderEvent.getProduct() : "Produce";

        log.info("Processing order event: farmerId={}, orderId={}, amount={}, product={}",
                farmerId, orderId, amount, product);

        // 1. Log order into order_log table
        OrderLog orderLog = OrderLog.builder()
                .farmerId(farmerId)
                .orderId(orderId)
                .amount(amount)
                .product(product)
                .recordedAt(LocalDateTime.now())
                .build();
        orderLogRepository.save(orderLog);

        // 2. Aggregate sales summary
        SalesSummary summary = salesSummaryRepository.findById(farmerId)
                .orElseGet(() -> SalesSummary.builder()
                        .farmerId(farmerId)
                        .totalOrders(0L)
                        .totalRevenue(BigDecimal.ZERO)
                        .lastUpdated(LocalDateTime.now())
                        .build());

        summary.setTotalOrders(summary.getTotalOrders() + 1);
        summary.setTotalRevenue(summary.getTotalRevenue().add(amount));
        summary.setLastUpdated(LocalDateTime.now());
        salesSummaryRepository.save(summary);

        log.info("Updated sales summary for farmer {}: totalOrders={}, totalRevenue={}",
                farmerId, summary.getTotalOrders(), summary.getTotalRevenue());

        // 3. Dispatch notification
        String notificationMessage = String.format("New order #%s received for '%s' valued at LKR %.2f.",
                orderId, product, amount);
        notificationService.dispatchAndSaveNotification(farmerId, "order-events", "IN_APP", notificationMessage);
    }
}
