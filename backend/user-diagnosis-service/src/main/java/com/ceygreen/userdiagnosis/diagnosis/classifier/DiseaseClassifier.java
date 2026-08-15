package com.ceygreen.userdiagnosis.diagnosis.classifier;

/**
 * Seam between the HTTP/persistence layer and the plant-disease model.
 *
 * <p>Swap implementations via {@code disease.classifier.impl} without touching the controller
 * or service. Today only {@link MockDiseaseClassifier} is wired; {@link OnnxDiseaseClassifier}
 * is a reserved stub for the trained model.
 */
public interface DiseaseClassifier {

    DiagnosisResult predict(byte[] imageBytes);
}
