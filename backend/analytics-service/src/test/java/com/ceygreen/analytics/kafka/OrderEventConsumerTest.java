package com.ceygreen.analytics.kafka;

import com.ceygreen.analytics.service.AnalyticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {

    @Mock
    private AnalyticsService analyticsService;

    private OrderEventConsumer orderEventConsumer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        orderEventConsumer = new OrderEventConsumer(analyticsService, objectMapper);
    }

    @Test
    void consume_delegatesToAnalyticsService() {
        String json = "{\"orderId\":1,\"farmerId\":\"FARMER-1\",\"cropName\":\"Tomato\",\"totalPrice\":500.00}";

        orderEventConsumer.consume(json);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(analyticsService).processOrderEvent(captor.capture());

        Map<String, Object> captured = captor.getValue();
        assertThat(captured.get("farmerId")).isEqualTo("FARMER-1");
        assertThat(captured.get("cropName")).isEqualTo("Tomato");
    }
}
