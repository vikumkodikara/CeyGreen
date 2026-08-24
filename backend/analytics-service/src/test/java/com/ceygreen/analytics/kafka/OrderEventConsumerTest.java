package com.ceygreen.analytics.kafka;

import com.ceygreen.analytics.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private OrderEventConsumer orderEventConsumer;

    @Test
    void consume_delegatesToAnalyticsService() {
        Map<String, Object> event = Map.of("orderId", 1L, "farmerId", "FARMER-1");

        orderEventConsumer.consume(event);

        verify(analyticsService).processOrderEvent(event);
    }
}
