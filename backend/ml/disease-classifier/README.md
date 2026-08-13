# Disease Classifier — ML Assets

This directory contains the trained machine learning model and its label mapping
used by the `user-diagnosis-service` for plant disease detection.

## Files

| File | Description |
|---|---|
| `disease_model.onnx` | ResNet50V2-based transfer-learning model exported from Keras via tf2onnx (~94 MB) |
| `labels.txt` | 25 class labels, one per line — line index N maps to output neuron N |

## Model Contract

- **Input tensor name:** `input`
- **Input shape:** `[1, 224, 224, 3]` (NHWC — batch, height, width, channels)
- **Input dtype:** float32
- **Input values:** Raw pixel values in 0–255 range (ResNet-style preprocessing is baked into the graph)
- **Output:** Softmax probability vector, one value per class in `labels.txt` order

## Usage

These files are copied into the service at:
```
user-diagnosis-service/src/main/resources/models/
```

The service loads them from the classpath at startup. To switch back to the mock
classifier (no model needed), set:
```yaml
disease.classifier.impl: mock
```

> **Warning:** Do NOT re-sort, deduplicate, or modify `labels.txt` — the line order
> must match the model's output indices exactly, or predictions will be wrong.
