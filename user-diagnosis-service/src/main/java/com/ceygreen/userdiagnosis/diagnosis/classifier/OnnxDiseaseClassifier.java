package com.ceygreen.userdiagnosis.diagnosis.classifier;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Reserved implementation for the trained ONNX model.
 *
 * <p>TODO: load {@code disease.classifier.model-path} with
 * {@code com.microsoft.onnxruntime.OrtEnvironment} / {@code OrtSession}, preprocess the image
 * bytes to the model's expected tensor shape, run inference, and map the output logits onto a
 * disease label + confidence. The onnxruntime dependency is already on the classpath so this
 * class can be filled in without touching the build file.
 *
 * <p>Not selected by default. Activate with {@code disease.classifier.impl=onnx} once the model
 * file is dropped into {@code src/main/resources/models/}.
 */
@Component
@ConditionalOnProperty(name = "disease.classifier.impl", havingValue = "onnx")
public class OnnxDiseaseClassifier implements DiseaseClassifier {

    @Override
    public DiagnosisResult predict(byte[] imageBytes) {
        throw new UnsupportedOperationException(
                "OnnxDiseaseClassifier is a stub. Drop the trained .onnx file into "
                        + "src/main/resources/models/ and implement predict() before setting "
                        + "disease.classifier.impl=onnx");
    }
}
