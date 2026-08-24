package com.ceygreen.analytics.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Periodically attempts to start Kafka listener containers.
 *
 * <p>Because {@code spring.kafka.listener.auto-startup=false} the service boots
 * without requiring Kafka.  This scheduler retries every 30 seconds until the
 * listeners are running.
 */
@Component
@EnableScheduling
public class KafkaAutoStartConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaAutoStartConfig.class);
    private final KafkaListenerEndpointRegistry registry;

    public KafkaAutoStartConfig(KafkaListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    @Scheduled(initialDelay = 5_000, fixedDelay = 30_000)
    public void startKafkaListeners() {
        for (MessageListenerContainer container : registry.getListenerContainers()) {
            if (!container.isRunning()) {
                try {
                    container.start();
                    log.info("Kafka listener '{}' started successfully.", container.getListenerId());
                } catch (Exception ex) {
                    log.warn("Kafka listener '{}' could not start (will retry in 30 s): {}",
                            container.getListenerId(), ex.getMessage());
                }
            }
        }
    }
}
