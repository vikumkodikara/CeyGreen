package com.ceygreen.diagnosis.diagnosis.classifier;

/** Outcome of a single inference call. */
public record DiagnosisResult(String predictedDisease, double confidenceScore) {
}
