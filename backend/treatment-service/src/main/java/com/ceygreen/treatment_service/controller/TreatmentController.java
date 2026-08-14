package com.ceygreen.treatment_service.controller;

import com.ceygreen.treatment_service.dto.TreatmentRequest;
import com.ceygreen.treatment_service.dto.TreatmentResponse;
import com.ceygreen.treatment_service.service.TreatmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/treatments")
@RequiredArgsConstructor

public class TreatmentController {

    private final TreatmentService treatmentService;

    @GetMapping("/{diseaseName}")
    public List<TreatmentResponse> getByDisease(@PathVariable String diseaseName) {
        return treatmentService.getTreatmentsByDisease(diseaseName);
    }

    @GetMapping("/search")
    public List<TreatmentResponse> search(
            @RequestParam(required = false) String crop,
            @RequestParam(required = false) String severity) {
        return treatmentService.searchTreatments(crop, severity);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public TreatmentResponse create(@Valid @RequestBody TreatmentRequest request) {
        return treatmentService.createTreatment(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TreatmentResponse update(@PathVariable Long id, @Valid @RequestBody TreatmentRequest request) {
        return treatmentService.updateTreatment(id, request);
    }

}
