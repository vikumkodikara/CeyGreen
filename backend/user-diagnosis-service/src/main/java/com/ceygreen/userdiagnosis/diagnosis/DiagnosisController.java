package com.ceygreen.userdiagnosis.diagnosis;

import com.ceygreen.userdiagnosis.common.ApiError;
import com.ceygreen.userdiagnosis.diagnosis.dto.DiagnosisResponse;
import com.ceygreen.userdiagnosis.security.CallerIdentity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/diagnosis")
@Tag(name = "Disease Detection", description = "Image upload, inference and diagnosis history")
@SecurityRequirement(name = "bearerAuth")
@SecurityRequirement(name = "apiKey")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload a leaf image for disease diagnosis",
            description = "Runs the configured DiseaseClassifier, persists the result, and publishes "
                    + "a diagnosis-events Kafka message. Does not call Treatment & Suggestion — the "
                    + "client decides whether to look up a treatment next.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Diagnosis created"),
            @ApiResponse(responseCode = "403", description = "farmerId does not match the authenticated user",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "415", description = "Unsupported image type",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public DiagnosisResponse upload(@RequestPart("image") MultipartFile image,
                                    @RequestPart("farmerId") String farmerId,
                                    @RequestPart("cropType") String cropType,
                                    Authentication authentication) {
        return diagnosisService.upload(image, UUID.fromString(farmerId.trim()), cropType,
                CallerIdentity.of(authentication));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a single past diagnosis")
    public DiagnosisResponse getById(@PathVariable String id, Authentication authentication) {
        return diagnosisService.getById(id, CallerIdentity.of(authentication));
    }

    @GetMapping("/history/{farmerId}")
    @Operation(summary = "List a farmer's past diagnoses")
    public List<DiagnosisResponse> history(@PathVariable UUID farmerId, Authentication authentication) {
        return diagnosisService.history(farmerId, CallerIdentity.of(authentication));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a diagnosis record")
    public void delete(@PathVariable String id, Authentication authentication) {
        diagnosisService.delete(id, CallerIdentity.of(authentication));
    }

    @GetMapping("/images/{filename}")
    @Operation(summary = "Serve a previously uploaded diagnosis image")
    public ResponseEntity<Resource> image(@PathVariable String filename) {
        Resource resource = diagnosisService.loadImage(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
