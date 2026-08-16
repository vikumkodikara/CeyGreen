package com.ceygreen.diagnosis.diagnosis;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * One uploaded leaf image and the classifier result for it.
 *
 * <p>{@code imageHash} is an extension beyond the Revision 3 field list: it enables the
 * "Ideas Worth Adding" identical-upload cache without changing the public API shape.
 */
@Document(collection = "diagnoses")
public class Diagnosis {

    @Id
    private String id;

    @Indexed
    private UUID farmerId;

    private String imageUrl;

    private String cropType;

    private String predictedDisease;

    private double confidenceScore;

    private Instant timestamp;

    /** SHA-256 of the uploaded bytes; used to short-circuit identical re-uploads. */
    @Indexed
    private String imageHash;

    public Diagnosis() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(UUID farmerId) {
        this.farmerId = farmerId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

    public String getPredictedDisease() {
        return predictedDisease;
    }

    public void setPredictedDisease(String predictedDisease) {
        this.predictedDisease = predictedDisease;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageHash() {
        return imageHash;
    }

    public void setImageHash(String imageHash) {
        this.imageHash = imageHash;
    }
}
