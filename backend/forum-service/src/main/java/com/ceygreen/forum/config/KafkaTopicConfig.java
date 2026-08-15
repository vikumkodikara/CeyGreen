package com.ceygreen.forum.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${ceygreen.kafka.forum-events-topic}") private String forumEventsTopic;
    @Value("${ceygreen.kafka.topic-partitions}") private int partitions;
    @Value("${ceygreen.kafka.topic-replicas}") private int replicas;

    @Bean
    public NewTopic forumEventsTopic() {
        return TopicBuilder.name(forumEventsTopic).partitions(partitions).replicas(replicas).build();
    }
}
