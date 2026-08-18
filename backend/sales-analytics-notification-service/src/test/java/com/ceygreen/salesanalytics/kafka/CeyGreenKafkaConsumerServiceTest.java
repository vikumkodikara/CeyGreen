package com.ceygreen.salesanalytics.kafka;

import com.ceygreen.salesanalytics.dto.event.OrderEventDto;
import com.ceygreen.salesanalytics.service.NotificationService;
import com.ceygreen.salesanalytics.service.OrderProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CeyGreenKafkaConsumerServiceTest {

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private OrderProcessingService orderProcessingService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CeyGreenKafkaConsumerService kafkaConsumerService;

    @Test
    void consumeOrderEvents_ProcessesOrderSuccessfully() {
        String jsonPayload = "{\"farmerId\":\"FARMER-101\",\"orderId\":\"ORD-2026-999\",\"amount\":15000.00,\"product\":\"Organic Tomatoes\"}";

        kafkaConsumerService.consumeOrderEvents(jsonPayload);

        verify(orderProcessingService, times(1)).processOrder(any(OrderEventDto.class));
    }

    @Test
    void consumeGreenhouseAlerts_DispatchesNotification() {
        String jsonPayload = "{\"userId\":\"FARMER-101\",\"greenhouseBay\":\"Bay-B1\",\"alertType\":\"HUMIDITY_HIGH\",\"severity\":\"CRITICAL\",\"message\":\"Humidity at 95%\"}";

        kafkaConsumerService.consumeGreenhouseAlerts(jsonPayload);

        verify(notificationService, times(1)).dispatchAndSaveNotification(
                eq("FARMER-101"), eq("greenhouse-alerts"), eq("IN_APP"), any(String.class));
    }

    @Test
    void consumeDiagnosisEvents_DispatchesNotification() {
        String jsonPayload = "{\"userId\":\"USER-001\",\"crop\":\"Strawberries\",\"diseaseDetected\":\"Leaf Spot\",\"confidence\":0.95,\"notes\":\"Apply organic copper spray\"}";

        kafkaConsumerService.consumeDiagnosisEvents(jsonPayload);

        verify(notificationService, times(1)).dispatchAndSaveNotification(
                eq("USER-001"), eq("diagnosis-events"), eq("IN_APP"), any(String.class));
    }

    @Test
    void consumeTreatmentEvents_DispatchesNotification() {
        String jsonPayload = "{\"userId\":\"USER-001\",\"crop\":\"Tomatoes\",\"treatmentApplied\":\"Bio-Fungicide\",\"dosage\":\"5ml/L\",\"status\":\"COMPLETED\"}";

        kafkaConsumerService.consumeTreatmentEvents(jsonPayload);

        verify(notificationService, times(1)).dispatchAndSaveNotification(
                eq("USER-001"), eq("treatment-events"), eq("IN_APP"), any(String.class));
    }

    @Test
    void consumeStockEvents_DispatchesNotification() {
        String jsonPayload = "{\"userId\":\"FARMER-101\",\"itemSku\":\"SKU-CUC-01\",\"itemName\":\"Cucumbers\",\"currentQuantity\":10.0,\"threshold\":25.0,\"eventType\":\"LOW_STOCK\"}";

        kafkaConsumerService.consumeStockEvents(jsonPayload);

        verify(notificationService, times(1)).dispatchAndSaveNotification(
                eq("FARMER-101"), eq("stock-events"), eq("IN_APP"), any(String.class));
    }

    @Test
    void consumeForumEvents_DispatchesNotification() {
        String jsonPayload = "{\"userId\":\"USER-002\",\"authorName\":\"Dilshan\",\"topic\":\"Hydroponics\",\"content\":\"How often do you flush nutrient tanks?\",\"eventType\":\"NEW_POST\"}";

        kafkaConsumerService.consumeForumEvents(jsonPayload);

        verify(notificationService, times(1)).dispatchAndSaveNotification(
                eq("USER-002"), eq("forum-events"), eq("IN_APP"), any(String.class));
    }
}
