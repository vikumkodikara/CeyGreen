package com.ceygreen.diagnosis.diagnosis;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "disease.classifier")
public class DiseaseClassifierProperties {

    @NotBlank
    private String impl = "mock";

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private double confidenceThreshold = 0.6;

    @NotBlank
    private String modelPath = "classpath:models/disease_model.onnx";

    @NotBlank
    private String labelsPath = "classpath:models/labels.txt";

    public String getImpl() {
        return impl;
    }

    public void setImpl(String impl) {
        this.impl = impl;
    }

    public double getConfidenceThreshold() {
        return confidenceThreshold;
    }

    public void setConfidenceThreshold(double confidenceThreshold) {
        this.confidenceThreshold = confidenceThreshold;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public String getLabelsPath() {
        return labelsPath;
    }

    public void setLabelsPath(String labelsPath) {
        this.labelsPath = labelsPath;
    }
}
