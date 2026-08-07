package com.ceygreen.iot.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableConfigurationProperties(KafkaAlertProperties.class)
public class KafkaTopicConfig {

    @Bean
    public NewTopic greenhouseAlertsTopic(KafkaAlertProperties properties) {
        return TopicBuilder.name(properties.getAlertTopic())
                .partitions(properties.getTopicPartitions())
                .replicas(properties.getTopicReplicas())
                .build();
    }
}
