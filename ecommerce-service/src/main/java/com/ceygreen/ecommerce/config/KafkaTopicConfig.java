package com.ceygreen.ecommerce.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${ceygreen.kafka.order-events-topic}") private String orderEventsTopic;
    @Value("${ceygreen.kafka.stock-events-topic}") private String stockEventsTopic;
    @Value("${ceygreen.kafka.topic-partitions}") private int partitions;
    @Value("${ceygreen.kafka.topic-replicas}") private int replicas;

    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder.name(orderEventsTopic).partitions(partitions).replicas(replicas).build();
    }

    @Bean
    public NewTopic stockEventsTopic() {
        return TopicBuilder.name(stockEventsTopic).partitions(partitions).replicas(replicas).build();
    }
}
