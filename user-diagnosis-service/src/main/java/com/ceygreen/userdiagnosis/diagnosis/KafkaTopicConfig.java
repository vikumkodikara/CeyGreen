package com.ceygreen.userdiagnosis.diagnosis;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableConfigurationProperties(KafkaDiagnosisProperties.class)
public class KafkaTopicConfig {

    @Bean
    public NewTopic diagnosisEventsTopic(KafkaDiagnosisProperties properties) {
        return TopicBuilder.name(properties.getDiagnosisTopic())
                .partitions(properties.getTopicPartitions())
                .replicas(properties.getTopicReplicas())
                .build();
    }
}
