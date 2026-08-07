package com.ceygreen.iot.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ceygreen.kafka")
public class KafkaAlertProperties {

    private String alertTopic = "greenhouse-alerts";
    private int topicPartitions = 1;
    private int topicReplicas = 1;

    public String getAlertTopic() {
        return alertTopic;
    }

    public void setAlertTopic(String alertTopic) {
        this.alertTopic = alertTopic;
    }

    public int getTopicPartitions() {
        return topicPartitions;
    }

    public void setTopicPartitions(int topicPartitions) {
        this.topicPartitions = topicPartitions;
    }

    public int getTopicReplicas() {
        return topicReplicas;
    }

    public void setTopicReplicas(int topicReplicas) {
        this.topicReplicas = topicReplicas;
    }
}
