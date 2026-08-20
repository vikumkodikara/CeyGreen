package com.ceygreen.treatment_service.controller;

import com.ceygreen.treatment_service.dto.RatingRequest;
import com.ceygreen.treatment_service.dto.TreatmentRequest;
import com.ceygreen.treatment_service.dto.TreatmentResponse;
import com.ceygreen.treatment_service.service.TreatmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/treatments")
@RequiredArgsConstructor
@Tag(name = "Treatments")
@SecurityRequirement(name = "apiKey")
public class TreatmentController {

    private final TreatmentService treatmentService;

    @GetMapping("/{diseaseName}")
    public List<TreatmentResponse> getByDisease(@PathVariable String diseaseName) {
        return treatmentService.getTreatmentsByDisease(diseaseName);
    }

    @GetMapping("/search")
    public List<TreatmentResponse> search(
            @RequestParam(required = false) String crop,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String type) {
        return treatmentService.searchTreatments(crop, severity, type);
    }

    @GetMapping("/crop/{cropName}")
    public List<TreatmentResponse> getByCrop(@PathVariable String cropName) {
        return treatmentService.searchTreatments(cropName, null, null);
    }

    @PostMapping("/{id}/rate")
    @ResponseStatus(HttpStatus.OK)
    public void rateTreatment(@PathVariable Long id, @Valid @RequestBody RatingRequest request) {
        treatmentService.rateTreatment(id, request);
    }

    @GetMapping("/{id}/alternatives")
    public List<TreatmentResponse> getAlternatives(@PathVariable Long id) {
        return treatmentService.getAlternatives(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARMER')")
    public TreatmentResponse create(@Valid @RequestBody TreatmentRequest request) {
        return treatmentService.createTreatment(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARMER')")
    public TreatmentResponse update(@PathVariable Long id, @Valid @RequestBody TreatmentRequest request) {
        return treatmentService.updateTreatment(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or hasRole('FARMER')")
    public void deleteTreatment(@PathVariable Long id, @RequestParam String farmerId) {
        treatmentService.deleteTreatment(id, farmerId);
    }

}
