package com.ceygreen.diagnosis.diagnosis;

import com.ceygreen.diagnosis.common.ApiException;
import com.ceygreen.diagnosis.diagnosis.classifier.DiagnosisResult;
import com.ceygreen.diagnosis.diagnosis.classifier.DiseaseClassifier;
import com.ceygreen.diagnosis.diagnosis.dto.DiagnosisResponse;
import com.ceygreen.diagnosis.security.CallerIdentity;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DiagnosisService {

    static final String UNCERTAIN_LABEL = "uncertain - consult an expert";

    private static final Logger log = LoggerFactory.getLogger(DiagnosisService.class);

    private final DiagnosisRepository diagnosisRepository;
    private final DiseaseClassifier diseaseClassifier;
    private final DiseaseClassifierProperties classifierProperties;
    private final DiseaseUploadProperties uploadProperties;
    private final ImageStorageService imageStorageService;
    private final DiagnosisEventPublisher eventPublisher;

    public DiagnosisService(DiagnosisRepository diagnosisRepository,
                            DiseaseClassifier diseaseClassifier,
                            DiseaseClassifierProperties classifierProperties,
                            DiseaseUploadProperties uploadProperties,
                            ImageStorageService imageStorageService,
                            DiagnosisEventPublisher eventPublisher) {
        this.diagnosisRepository = diagnosisRepository;
        this.diseaseClassifier = diseaseClassifier;
        this.classifierProperties = classifierProperties;
        this.uploadProperties = uploadProperties;
        this.imageStorageService = imageStorageService;
        this.eventPublisher = eventPublisher;
    }

    public DiagnosisResponse upload(MultipartFile image, UUID farmerId, String cropType,
                                    CallerIdentity caller) {
        caller.requireCanActAs(farmerId, "upload a diagnosis");
        if (!StringUtils.hasText(cropType)) {
            throw ApiException.badRequest("cropType is required");
        }

        byte[] bytes = readAndValidate(image);
        String imageHash = sha256(bytes);

        if (uploadProperties.isCacheIdenticalUploads()) {
            var cached = diagnosisRepository.findByImageHashAndFarmerId(imageHash, farmerId);
            if (cached.isPresent()) {
                log.info("Returning cached diagnosis {} for identical upload by {}",
                        cached.get().getId(), farmerId);
                return DiagnosisResponse.from(cached.get());
            }
        }

        DiagnosisResult raw = diseaseClassifier.predict(bytes);
        String predicted = raw.predictedDisease();
        double confidence = raw.confidenceScore();
        if (confidence < classifierProperties.getConfidenceThreshold()) {
            predicted = UNCERTAIN_LABEL;
        }

        ImageStorageService.StoredImage stored = imageStorageService.store(bytes, image.getContentType());

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setFarmerId(farmerId);
        diagnosis.setImageUrl(stored.url());
        diagnosis.setCropType(cropType.trim());
        diagnosis.setPredictedDisease(predicted);
        diagnosis.setConfidenceScore(confidence);
        diagnosis.setTimestamp(Instant.now());
        diagnosis.setImageHash(imageHash);

        Diagnosis saved = diagnosisRepository.save(diagnosis);
        eventPublisher.publish(saved);
        return DiagnosisResponse.from(saved);
    }

    public DiagnosisResponse getById(String id, CallerIdentity caller) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Diagnosis not found"));
        caller.requireCanActAs(diagnosis.getFarmerId(), "view this diagnosis");
        return DiagnosisResponse.from(diagnosis);
    }

    public List<DiagnosisResponse> history(UUID farmerId, CallerIdentity caller) {
        caller.requireCanActAs(farmerId, "view diagnosis history");
        return diagnosisRepository.findByFarmerIdOrderByTimestampDesc(farmerId).stream()
                .map(DiagnosisResponse::from)
                .toList();
    }

    public void delete(String id, CallerIdentity caller) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Diagnosis not found"));
        caller.requireCanActAs(diagnosis.getFarmerId(), "delete this diagnosis");
        diagnosisRepository.delete(diagnosis);
        imageStorageService.deleteIfPresent(diagnosis.getImageUrl());
    }

    public Resource loadImage(String filename) {
        return imageStorageService.load(filename);
    }

    private byte[] readAndValidate(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw ApiException.badRequest("An image file is required");
        }
        String contentType = image.getContentType();
        if (contentType == null
                || !uploadProperties.getAllowedContentTypes().contains(contentType.toLowerCase(Locale.ROOT))) {
            throw ApiException.unsupportedMediaType(
                    "Unsupported image type. Allowed: " + String.join(", ",
                            uploadProperties.getAllowedContentTypes()));
        }
        if (image.getSize() > uploadProperties.getMaxFileSizeBytes()) {
            throw ApiException.payloadTooLarge("Uploaded image exceeds the maximum permitted size");
        }
        try {
            byte[] bytes = image.getBytes();
            if (bytes.length == 0) {
                throw ApiException.badRequest("An image file is required");
            }
            if (bytes.length > uploadProperties.getMaxFileSizeBytes()) {
                throw ApiException.payloadTooLarge("Uploaded image exceeds the maximum permitted size");
            }
            return bytes;
        } catch (ApiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ApiException.badRequest("Failed to read uploaded image");
        }
    }

    static String sha256(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }

    /** Exposed for tests that need to craft a low-confidence image. */
    public Set<String> allowedContentTypes() {
        return Set.copyOf(uploadProperties.getAllowedContentTypes());
    }
}
