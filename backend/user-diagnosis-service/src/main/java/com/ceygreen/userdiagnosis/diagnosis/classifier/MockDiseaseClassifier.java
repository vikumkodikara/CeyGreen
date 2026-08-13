package com.ceygreen.userdiagnosis.diagnosis.classifier;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Deterministic stand-in for the trained model so the service is fully demoable without a
 * .onnx file. The same image bytes always produce the same disease and confidence.
 */
@Component
@ConditionalOnProperty(name = "disease.classifier.impl", havingValue = "mock", matchIfMissing = true)
public class MockDiseaseClassifier implements DiseaseClassifier {

    static final List<String> DISEASES = List.of(
            "Tomato Early Blight",
            "Tomato Late Blight",
            "Tomato Leaf Mold",
            "Potato Early Blight",
            "Potato Late Blight",
            "Pepper Bacterial Spot",
            "Healthy");

    @Override
    public DiagnosisResult predict(byte[] imageBytes) {
        int hash = stableHash(imageBytes);
        String disease = DISEASES.get(Math.floorMod(hash, DISEASES.size()));
        // Map the hash into [0.45, 0.98] so both the low-confidence and high-confidence paths
        // are reachable in demos without needing a real model.
        double confidence = 0.45 + (Math.floorMod(hash >>> 8, 54) / 100.0);
        return new DiagnosisResult(disease, confidence);
    }

    private static int stableHash(byte[] imageBytes) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(imageBytes);
            return ((digest[0] & 0xff) << 24)
                    | ((digest[1] & 0xff) << 16)
                    | ((digest[2] & 0xff) << 8)
                    | (digest[3] & 0xff);
        } catch (NoSuchAlgorithmException ex) {
            return imageBytes.length ^ new String(imageBytes, 0, Math.min(16, imageBytes.length),
                    StandardCharsets.ISO_8859_1).hashCode();
        }
    }
}
