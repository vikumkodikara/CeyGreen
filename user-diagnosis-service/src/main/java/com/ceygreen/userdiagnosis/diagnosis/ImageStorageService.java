package com.ceygreen.userdiagnosis.diagnosis;

import com.ceygreen.userdiagnosis.common.ApiException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ImageStorageService {

    private final DiseaseUploadProperties properties;
    private Path root;

    public ImageStorageService(DiseaseUploadProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void init() throws IOException {
        root = Path.of(properties.getDirectory()).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    public StoredImage store(byte[] bytes, String contentType) {
        String extension = extensionFor(contentType);
        String filename = UUID.randomUUID() + extension;
        Path target = root.resolve(filename);
        try {
            Files.write(target, bytes);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store diagnosis image", ex);
        }
        return new StoredImage("/diagnosis/images/" + filename, filename);
    }

    public Resource load(String filename) {
        Path resolved = root.resolve(filename).normalize();
        if (!resolved.startsWith(root) || !Files.isRegularFile(resolved)) {
            throw ApiException.notFound("Diagnosis image not found");
        }
        return new FileSystemResource(resolved);
    }

    public void deleteIfPresent(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith("/diagnosis/images/")) {
            return;
        }
        String filename = imageUrl.substring("/diagnosis/images/".length());
        Path resolved = root.resolve(filename).normalize();
        if (resolved.startsWith(root)) {
            try {
                Files.deleteIfExists(resolved);
            } catch (IOException ignored) {
                // Best-effort cleanup; the Mongo record is the source of truth.
            }
        }
    }

    private static String extensionFor(String contentType) {
        if (contentType == null) {
            return ".bin";
        }
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".bin";
        };
    }

    public record StoredImage(String url, String filename) {
    }
}
