package com.ceygreen.salesanalytics.kafka;

import com.ceygreen.salesanalytics.dto.event.*;
import com.ceygreen.salesanalytics.service.NotificationService;
import com.ceygreen.salesanalytics.service.OrderProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class CeyGreenKafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(CeyGreenKafkaConsumerService.class);

    private final ObjectMapper objectMapper;
    private final OrderProcessingService orderProcessingService;
    private final NotificationService notificationService;

    public CeyGreenKafkaConsumerService(ObjectMapper objectMapper,
                                        OrderProcessingService orderProcessingService,
                                        NotificationService notificationService) {
        this.objectMapper = objectMapper;
        this.orderProcessingService = orderProcessingService;
        this.notificationService = notificationService;
    }

    /**
     * 1. Consume from order-events
     */
    @KafkaListener(topics = "${ceygreen.kafka.topics.order-events:order-events}", groupId = "${spring.kafka.consumer.group-id:sales-analytics-notification-group}")
    public void consumeOrderEvents(String message) {
        log.info("[KAFKA CONSUMER] Received message on topic 'order-events': {}", message);
        try {
            OrderEventDto event = objectMapper.readValue(message, OrderEventDto.class);
            orderProcessingService.processOrder(event);
        } catch (Exception e) {
            log.error("Failed to process order-events message: {}", message, e);
        }
    }

    /**
     * 2. Consume from greenhouse-alerts
     */
    @KafkaListener(topics = "${ceygreen.kafka.topics.greenhouse-alerts:greenhouse-alerts}", groupId = "${spring.kafka.consumer.group-id:sales-analytics-notification-group}")
    public void consumeGreenhouseAlerts(String message) {
        log.info("[KAFKA CONSUMER] Received message on topic 'greenhouse-alerts': {}", message);
        try {
            GreenhouseAlertDto alert = objectMapper.readValue(message, GreenhouseAlertDto.class);
            String targetUser = alert.getUserId() != null ? alert.getUserId() : alert.getFarmerId();
            String formattedMessage = String.format("ALERT [%s] in %s: %s (Severity: %s)",
                    alert.getAlertType() != null ? alert.getAlertType() : "ENVIRONMENT_WARNING",
                    alert.getGreenhouseBay() != null ? alert.getGreenhouseBay() : "Greenhouse",
                    alert.getMessage() != null ? alert.getMessage() : "Sensor threshold triggered",
                    alert.getSeverity() != null ? alert.getSeverity() : "HIGH");

            notificationService.dispatchAndSaveNotification(targetUser, "greenhouse-alerts", "IN_APP", formattedMessage);
        } catch (Exception e) {
            log.error("Failed to process greenhouse-alerts message: {}", message, e);
        }
    }

    /**
     * 3. Consume from diagnosis-events
     */
    @KafkaListener(topics = "${ceygreen.kafka.topics.diagnosis-events:diagnosis-events}", groupId = "${spring.kafka.consumer.group-id:sales-analytics-notification-group}")
    public void consumeDiagnosisEvents(String message) {
        log.info("[KAFKA CONSUMER] Received message on topic 'diagnosis-events': {}", message);
        try {
            DiagnosisEventDto event = objectMapper.readValue(message, DiagnosisEventDto.class);
            String targetUser = event.getUserId() != null ? event.getUserId() : event.getFarmerId();
            String formattedMessage = String.format("Crop Health Diagnosis: Disease '%s' identified on %s (Confidence: %.1f%%). Notes: %s",
                    event.getDiseaseDetected() != null ? event.getDiseaseDetected() : "Unknown Issue",
                    event.getCrop() != null ? event.getCrop() : "Plant",
                    event.getConfidence() != null ? event.getConfidence() * 100 : 90.0,
                    event.getNotes() != null ? event.getNotes() : "Review recommended treatments in app.");

            notificationService.dispatchAndSaveNotification(targetUser, "diagnosis-events", "IN_APP", formattedMessage);
        } catch (Exception e) {
            log.error("Failed to process diagnosis-events message: {}", message, e);
        }
    }

    /**
     * 4. Consume from treatment-events
     */
    @KafkaListener(topics = "${ceygreen.kafka.topics.treatment-events:treatment-events}", groupId = "${spring.kafka.consumer.group-id:sales-analytics-notification-group}")
    public void consumeTreatmentEvents(String message) {
        log.info("[KAFKA CONSUMER] Received message on topic 'treatment-events': {}", message);
        try {
            TreatmentEventDto event = objectMapper.readValue(message, TreatmentEventDto.class);
            String targetUser = event.getUserId() != null ? event.getUserId() : event.getFarmerId();
            String formattedMessage = String.format("Treatment Protocol Update: Applied '%s' (Dosage: %s) to %s. Status: %s.",
                    event.getTreatmentApplied() != null ? event.getTreatmentApplied() : "Prescribed Formula",
                    event.getDosage() != null ? event.getDosage() : "Standard",
                    event.getCrop() != null ? event.getCrop() : "Crops",
                    event.getStatus() != null ? event.getStatus() : "COMPLETED");

            notificationService.dispatchAndSaveNotification(targetUser, "treatment-events", "IN_APP", formattedMessage);
        } catch (Exception e) {
            log.error("Failed to process treatment-events message: {}", message, e);
        }
    }

    /**
     * 5. Consume from stock-events
     */
    @KafkaListener(topics = "${ceygreen.kafka.topics.stock-events:stock-events}", groupId = "${spring.kafka.consumer.group-id:sales-analytics-notification-group}")
    public void consumeStockEvents(String message) {
        log.info("[KAFKA CONSUMER] Received message on topic 'stock-events': {}", message);
        try {
            StockEventDto event = objectMapper.readValue(message, StockEventDto.class);
            String targetUser = event.getUserId() != null ? event.getUserId() : event.getFarmerId();
            String formattedMessage = String.format("Stock Alert [%s]: %s (SKU: %s) has current quantity %.2f (Threshold: %.2f).",
                    event.getEventType() != null ? event.getEventType() : "INVENTORY_UPDATE",
                    event.getItemName() != null ? event.getItemName() : "Produce",
                    event.getItemSku() != null ? event.getItemSku() : "N/A",
                    event.getCurrentQuantity() != null ? event.getCurrentQuantity() : 0.0,
                    event.getThreshold() != null ? event.getThreshold() : 0.0);

            notificationService.dispatchAndSaveNotification(targetUser, "stock-events", "IN_APP", formattedMessage);
        } catch (Exception e) {
            log.error("Failed to process stock-events message: {}", message, e);
        }
    }

    /**
     * 6. Consume from forum-events
     */
    @KafkaListener(topics = "${ceygreen.kafka.topics.forum-events:forum-events}", groupId = "${spring.kafka.consumer.group-id:sales-analytics-notification-group}")
    public void consumeForumEvents(String message) {
        log.info("[KAFKA CONSUMER] Received message on topic 'forum-events': {}", message);
        try {
            ForumEventDto event = objectMapper.readValue(message, ForumEventDto.class);
            String formattedMessage = String.format("Community Forum [%s]: %s posted in topic '%s': \"%s\"",
                    event.getEventType() != null ? event.getEventType() : "ACTIVITY",
                    event.getAuthorName() != null ? event.getAuthorName() : "A community member",
                    event.getTopic() != null ? event.getTopic() : "General Greenhouse Discussion",
                    event.getContent() != null && event.getContent().length() > 60 ? event.getContent().substring(0, 60) + "..." : event.getContent());

            notificationService.dispatchAndSaveNotification(event.getUserId(), "forum-events", "IN_APP", formattedMessage);
        } catch (Exception e) {
            log.error("Failed to process forum-events message: {}", message, e);
        }
    }
}
