package com.ceygreen.salesanalytics.config;

import com.ceygreen.salesanalytics.domain.entity.Notification;
import com.ceygreen.salesanalytics.domain.entity.NotificationPreference;
import com.ceygreen.salesanalytics.domain.entity.OrderLog;
import com.ceygreen.salesanalytics.domain.entity.SalesSummary;
import com.ceygreen.salesanalytics.domain.repository.NotificationPreferenceRepository;
import com.ceygreen.salesanalytics.domain.repository.NotificationRepository;
import com.ceygreen.salesanalytics.domain.repository.OrderLogRepository;
import com.ceygreen.salesanalytics.domain.repository.SalesSummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final SalesSummaryRepository salesSummaryRepository;
    private final OrderLogRepository orderLogRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    public DataInitializer(SalesSummaryRepository salesSummaryRepository,
                           OrderLogRepository orderLogRepository,
                           NotificationRepository notificationRepository,
                           NotificationPreferenceRepository preferenceRepository) {
        this.salesSummaryRepository = salesSummaryRepository;
        this.orderLogRepository = orderLogRepository;
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
    }

    @Override
    public void run(String... args) {
        if (salesSummaryRepository.count() > 0) {
            log.info("CeyGreen database already contains seed data. Skipping initialization.");
            return;
        }

        log.info("=== Initializing CeyGreen Sales Analytics & Notification Seed Data ===");

        // 1. Seed Initial Sales Summaries
        SalesSummary farmer1 = SalesSummary.builder()
                .farmerId("FARMER-101")
                .totalOrders(12L)
                .totalRevenue(BigDecimal.valueOf(148500.00))
                .lastUpdated(LocalDateTime.now().minusHours(2))
                .build();

        SalesSummary farmer2 = SalesSummary.builder()
                .farmerId("FARMER-102")
                .totalOrders(8L)
                .totalRevenue(BigDecimal.valueOf(96200.00))
                .lastUpdated(LocalDateTime.now().minusHours(5))
                .build();

        SalesSummary farmer3 = SalesSummary.builder()
                .farmerId("FARMER-103")
                .totalOrders(19L)
                .totalRevenue(BigDecimal.valueOf(234000.00))
                .lastUpdated(LocalDateTime.now().minusMinutes(30))
                .build();

        salesSummaryRepository.saveAll(List.of(farmer1, farmer2, farmer3));

        // 2. Seed Initial Order Logs for FARMER-101
        OrderLog log1 = OrderLog.builder()
                .farmerId("FARMER-101")
                .orderId("ORD-2026-001")
                .amount(BigDecimal.valueOf(12500.00))
                .product("Vine-Ripened Cherry Tomatoes")
                .recordedAt(LocalDateTime.now().minusDays(5))
                .build();

        OrderLog log2 = OrderLog.builder()
                .farmerId("FARMER-101")
                .orderId("ORD-2026-002")
                .amount(BigDecimal.valueOf(24000.00))
                .product("Greenhouse Red Bell Peppers")
                .recordedAt(LocalDateTime.now().minusDays(3))
                .build();

        OrderLog log3 = OrderLog.builder()
                .farmerId("FARMER-101")
                .orderId("ORD-2026-003")
                .amount(BigDecimal.valueOf(18500.00))
                .product("Hydroponic Butterhead Lettuce")
                .recordedAt(LocalDateTime.now().minusHours(2))
                .build();

        orderLogRepository.saveAll(List.of(log1, log2, log3));

        // 3. Seed Notification Preferences
        NotificationPreference pref1 = NotificationPreference.builder()
                .userId("USER-001")
                .eventType("order-events")
                .channel("IN_APP")
                .enabled(true)
                .build();

        NotificationPreference pref2 = NotificationPreference.builder()
                .userId("USER-001")
                .eventType("greenhouse-alerts")
                .channel("SMS")
                .enabled(true)
                .build();

        NotificationPreference pref3 = NotificationPreference.builder()
                .userId("FARMER-101")
                .eventType("order-events")
                .channel("IN_APP")
                .enabled(true)
                .build();

        preferenceRepository.saveAll(List.of(pref1, pref2, pref3));

        // 4. Seed Notifications
        Notification notif1 = Notification.builder()
                .userId("FARMER-101")
                .sourceTopic("order-events")
                .channel("IN_APP")
                .message("New order #ORD-2026-003 received for 'Hydroponic Butterhead Lettuce' valued at LKR 18,500.00.")
                .sentAt(LocalDateTime.now().minusHours(2))
                .status("DELIVERED")
                .build();

        Notification notif2 = Notification.builder()
                .userId("FARMER-101")
                .sourceTopic("greenhouse-alerts")
                .channel("IN_APP")
                .message("ALERT [HIGH_TEMP] in Bay-A2: Temperature reached 31.2°C. Ventilation fans activated.")
                .sentAt(LocalDateTime.now().minusHours(8))
                .status("DELIVERED")
                .build();

        Notification notif3 = Notification.builder()
                .userId("USER-001")
                .sourceTopic("diagnosis-events")
                .channel("IN_APP")
                .message("Crop Health Diagnosis: Disease 'Powdery Mildew' identified on Tomatoes (Confidence: 94.2%).")
                .sentAt(LocalDateTime.now().minusDays(1))
                .status("DELIVERED")
                .build();

        notificationRepository.saveAll(List.of(notif1, notif2, notif3));

        log.info("=== CeyGreen Seed Data Initialization Complete! ===");
    }
}
