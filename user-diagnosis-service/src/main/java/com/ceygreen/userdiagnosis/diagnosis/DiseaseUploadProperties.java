package com.ceygreen.userdiagnosis.diagnosis;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "disease.upload")
public class DiseaseUploadProperties {

    @NotBlank
    private String directory = "./data/diagnosis-images";

    @Min(1)
    private long maxFileSizeBytes = 5_242_880L;

    @NotEmpty
    private List<String> allowedContentTypes = List.of("image/jpeg", "image/png", "image/webp");

    private boolean cacheIdenticalUploads = true;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public long getMaxFileSizeBytes() {
        return maxFileSizeBytes;
    }

    public void setMaxFileSizeBytes(long maxFileSizeBytes) {
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    public List<String> getAllowedContentTypes() {
        return allowedContentTypes;
    }

    public void setAllowedContentTypes(List<String> allowedContentTypes) {
        this.allowedContentTypes = allowedContentTypes;
    }

    public boolean isCacheIdenticalUploads() {
        return cacheIdenticalUploads;
    }

    public void setCacheIdenticalUploads(boolean cacheIdenticalUploads) {
        this.cacheIdenticalUploads = cacheIdenticalUploads;
    }
}
