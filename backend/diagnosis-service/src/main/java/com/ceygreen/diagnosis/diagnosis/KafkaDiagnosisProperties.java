package com.ceygreen.diagnosis.diagnosis;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ceygreen.kafka")
public class KafkaDiagnosisProperties {

    @NotBlank
    private String diagnosisTopic = "diagnosis-events";

    @Min(1)
    private int topicPartitions = 1;

    @Min(1)
    private int topicReplicas = 1;

    public String getDiagnosisTopic() {
        return diagnosisTopic;
    }

    public void setDiagnosisTopic(String diagnosisTopic) {
        this.diagnosisTopic = diagnosisTopic;
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
