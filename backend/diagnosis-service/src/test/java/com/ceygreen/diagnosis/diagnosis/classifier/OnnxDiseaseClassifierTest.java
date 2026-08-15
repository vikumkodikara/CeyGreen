package com.ceygreen.diagnosis.diagnosis.classifier;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.core.io.DefaultResourceLoader;
import com.ceygreen.diagnosis.diagnosis.DiseaseClassifierProperties;

/**
 * Standalone test for {@link OnnxDiseaseClassifier} — no Spring context, no DB,
 * no Kafka. Verifies the ONNX model loads from the classpath, accepts image bytes,
 * and returns a non-null prediction with a valid confidence score.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OnnxDiseaseClassifierTest {

    private OnnxDiseaseClassifier classifier;

    @BeforeAll
    void setUp() throws Exception {
        DiseaseClassifierProperties props = new DiseaseClassifierProperties();
        props.setImpl("onnx");
        props.setModelPath("classpath:models/disease_model.onnx");
        props.setLabelsPath("classpath:models/labels.txt");

        classifier = new OnnxDiseaseClassifier(new DefaultResourceLoader(), props);
        classifier.init();
    }

    @AfterAll
    void tearDown() {
        if (classifier != null) {
            classifier.destroy();
        }
    }

    @Test
    void predict_withSyntheticGreenImage_returnsValidResult() throws Exception {
        // Create a 300x300 green image (simulating a leaf)
        byte[] imageBytes = createTestImage(300, 300, new Color(34, 139, 34));

        DiagnosisResult result = classifier.predict(imageBytes);

        assertNotNull(result, "Prediction result should not be null");
        assertNotNull(result.predictedDisease(), "Predicted disease should not be null");
        assertFalse(result.predictedDisease().isBlank(), "Predicted disease should not be blank");
        assertTrue(result.confidenceScore() >= 0.0 && result.confidenceScore() <= 1.0,
                "Confidence must be in [0,1], got: " + result.confidenceScore());

        System.out.println("=== ONNX Prediction Test Result ===");
        System.out.println("Predicted disease: " + result.predictedDisease());
        System.out.println("Confidence score:  " + result.confidenceScore());
    }

    @Test
    void predict_withSmallImage_resizesAndPredicts() throws Exception {
        // Tiny image to test resize handling
        byte[] imageBytes = createTestImage(32, 32, Color.RED);

        DiagnosisResult result = classifier.predict(imageBytes);

        assertNotNull(result);
        assertNotNull(result.predictedDisease());
        assertTrue(result.confidenceScore() >= 0.0 && result.confidenceScore() <= 1.0);
    }

    @Test
    void predict_withLargeImage_resizesAndPredicts() throws Exception {
        // Large image to test downscale handling
        byte[] imageBytes = createTestImage(1024, 768, Color.YELLOW);

        DiagnosisResult result = classifier.predict(imageBytes);

        assertNotNull(result);
        assertNotNull(result.predictedDisease());
        assertTrue(result.confidenceScore() >= 0.0 && result.confidenceScore() <= 1.0);
    }

    private static byte[] createTestImage(int width, int height, Color color) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        try {
            g2d.setColor(color);
            g2d.fillRect(0, 0, width, height);
        } finally {
            g2d.dispose();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
