package com.ceygreen.ecommerce.service;

import com.ceygreen.ecommerce.dto.DashboardSummaryResponse;
import com.ceygreen.ecommerce.entity.OrderStatus;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ProductService productService;
    private final OrderService orderService;

    public DashboardService(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    public DashboardSummaryResponse getSummary(UUID farmerId) {
        long totalProducts = productService.countByFarmer(farmerId);
        long activeProducts = productService.countActiveByFarmer(farmerId);
        long inactiveProducts = productService.countInactiveByFarmer(farmerId);
        long lowStockProducts = productService.countLowStockByFarmer(farmerId);
        long totalOrders = orderService.countByFarmer(farmerId);
        long pendingOrders = orderService.countPendingByFarmer(farmerId);
        long completedOrders = orderService.countDeliveredByFarmer(farmerId);
        BigDecimal totalRevenue = orderService.revenueByFarmer(farmerId);
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }
        return new DashboardSummaryResponse(
                totalProducts,
                activeProducts,
                inactiveProducts,
                lowStockProducts,
                totalOrders,
                pendingOrders,
                completedOrders,
                totalRevenue);
    }
}
