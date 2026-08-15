package com.ceygreen.userdiagnosis.diagnosis.classifier;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import com.ceygreen.userdiagnosis.diagnosis.DiseaseClassifierProperties;
import jakarta.annotation.PostConstruct;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

/**
 * ONNX Runtime-based plant disease classifier using a ResNet50V2 transfer-learning model.
 *
 * <p>The model is loaded once at startup and reused across all requests. The ONNX graph already
 * includes ResNet-style channel normalisation, so input pixel values are fed as raw 0–255 floats
 * without any additional scaling.
 *
 * <p>Activate with {@code disease.classifier.impl=onnx} (now the default).
 */
@Component
@ConditionalOnProperty(name = "disease.classifier.impl", havingValue = "onnx")
public class OnnxDiseaseClassifier implements DiseaseClassifier, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(OnnxDiseaseClassifier.class);

    private static final String INPUT_TENSOR_NAME = "input";
    private static final int IMAGE_SIZE = 224;
    private static final int CHANNELS = 3;

    private final ResourceLoader resourceLoader;
    private final DiseaseClassifierProperties properties;

    private OrtEnvironment environment;
    private OrtSession session;
    private List<String> labels;

    /** Temp file reference so we can clean it up on shutdown. */
    private Path tempModelFile;

    public OnnxDiseaseClassifier(ResourceLoader resourceLoader,
                                  DiseaseClassifierProperties properties) {
        this.resourceLoader = resourceLoader;
        this.properties = properties;
    }

    @PostConstruct
    void init() throws OrtException, IOException {
        log.info("Loading ONNX disease model from {}", properties.getModelPath());

        // OrtSession requires a file path — classpath resources inside JARs are not directly
        // addressable, so we copy to a temp file that is cleaned up on shutdown.
        tempModelFile = Files.createTempFile("disease_model_", ".onnx");
        try (InputStream modelStream = resourceLoader.getResource(properties.getModelPath())
                .getInputStream()) {
            Files.copy(modelStream, tempModelFile, StandardCopyOption.REPLACE_EXISTING);
        }

        environment = OrtEnvironment.getEnvironment();
        session = environment.createSession(tempModelFile.toString());
        log.info("ONNX session created — input(s): {}, output(s): {}",
                session.getInputNames(), session.getOutputNames());

        // Load labels — one class name per line, order matches the model's output indices.
        try (InputStream labelsStream = resourceLoader.getResource(properties.getLabelsPath())
                .getInputStream()) {
            labels = Collections.unmodifiableList(
                    new String(labelsStream.readAllBytes(), StandardCharsets.UTF_8)
                            .lines()
                            .toList());
        }
        log.info("Loaded {} disease labels from {}", labels.size(), properties.getLabelsPath());
    }

    @Override
    public DiagnosisResult predict(byte[] imageBytes) {
        try {
            float[][][][] tensor = preprocessImage(imageBytes);
            return runInference(tensor);
        } catch (OrtException ex) {
            throw new IllegalStateException("ONNX inference failed", ex);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to decode uploaded image", ex);
        }
    }

    @Override
    public void destroy() {
        log.info("Shutting down ONNX disease classifier");
        if (session != null) {
            try {
                session.close();
            } catch (OrtException ex) {
                log.warn("Error closing ONNX session", ex);
            }
        }
        if (tempModelFile != null) {
            try {
                Files.deleteIfExists(tempModelFile);
            } catch (IOException ex) {
                log.warn("Failed to delete temp model file {}", tempModelFile, ex);
            }
        }
    }

    // ---- internals ----

    /**
     * Decodes the uploaded image bytes, resizes to 224×224, and builds an NHWC float tensor
     * with raw 0–255 pixel values (the ONNX graph contains its own preprocessing).
     */
    private float[][][][] preprocessImage(byte[] imageBytes) throws IOException {
        BufferedImage original = ImageIO.read(new ByteArrayInputStream(imageBytes));
        if (original == null) {
            throw new IOException("ImageIO could not decode the uploaded image — "
                    + "the file may be corrupt or an unsupported format");
        }

        BufferedImage resized = resize(original, IMAGE_SIZE, IMAGE_SIZE);

        float[][][][] tensor = new float[1][IMAGE_SIZE][IMAGE_SIZE][CHANNELS];
        for (int y = 0; y < IMAGE_SIZE; y++) {
            for (int x = 0; x < IMAGE_SIZE; x++) {
                int rgb = resized.getRGB(x, y);
                tensor[0][y][x][0] = (rgb >> 16) & 0xFF;  // R
                tensor[0][y][x][1] = (rgb >> 8) & 0xFF;   // G
                tensor[0][y][x][2] = rgb & 0xFF;           // B
            }
        }
        return tensor;
    }

    /**
     * Resizes the image to exactly {@code targetW × targetH} using bilinear interpolation,
     * converting to RGB if necessary (handles ARGB, grayscale, etc.).
     */
    private static BufferedImage resize(BufferedImage src, int targetW, int targetH) {
        BufferedImage dest = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = dest.createGraphics();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            g2d.drawImage(src, 0, 0, targetW, targetH, null);
        } finally {
            g2d.dispose();
        }
        return dest;
    }

    /**
     * Runs the ONNX session, extracts the softmax output, and returns the top prediction.
     */
    private DiagnosisResult runInference(float[][][][] tensor) throws OrtException {
        try (OnnxTensor inputTensor = OnnxTensor.createTensor(environment, tensor);
             OrtSession.Result result = session.run(
                     Map.of(INPUT_TENSOR_NAME, inputTensor))) {

            float[][] output = (float[][]) result.get(0).getValue();
            float[] probabilities = output[0];

            int bestIndex = 0;
            float bestScore = probabilities[0];
            for (int i = 1; i < probabilities.length; i++) {
                if (probabilities[i] > bestScore) {
                    bestScore = probabilities[i];
                    bestIndex = i;
                }
            }

            String diseaseName = bestIndex < labels.size()
                    ? labels.get(bestIndex)
                    : "Unknown (index " + bestIndex + ")";

            log.info("Prediction: {} (confidence: {}, index: {})",
                    diseaseName, String.format("%.4f", bestScore), bestIndex);

            return new DiagnosisResult(diseaseName, bestScore);
        }
    }
}
