package com.ceygreen.treatment.controller;

import com.ceygreen.treatment.dto.TreatmentRequest;
import com.ceygreen.treatment.dto.TreatmentResponse;
import com.ceygreen.treatment.dto.TreatmentUpdateRequest;
import com.ceygreen.treatment.service.TreatmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/treatments")
public class TreatmentController {

    private final TreatmentService treatmentService;

    public TreatmentController(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    /** Fetch all recommended treatments for a given disease name. */
    @GetMapping("/{diseaseName}")
    public ResponseEntity<List<TreatmentResponse>> getTreatments(@PathVariable String diseaseName) {
        return ResponseEntity.ok(treatmentService.getTreatmentsByDisease(diseaseName));
    }

    /** Filter treatments by crop type and/or severity (type). */
    @GetMapping("/search")
    public ResponseEntity<List<TreatmentResponse>> searchTreatments(
            @RequestParam(required = false) String crop,
            @RequestParam(required = false) String severity) {
        return ResponseEntity.ok(treatmentService.searchTreatments(crop, severity));
    }

    /** Add a new treatment entry (admin/curator). */
    @PostMapping
    public ResponseEntity<TreatmentResponse> createTreatment(@Valid @RequestBody TreatmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(treatmentService.createTreatment(request));
    }

    /** Update dosage, frequency, or safety notes. Set active=false to soft-delete. */
    @PutMapping("/{id}")
    public ResponseEntity<TreatmentResponse> updateTreatment(@PathVariable Long id,
                                                              @RequestBody TreatmentUpdateRequest request) {
        return ResponseEntity.ok(treatmentService.updateTreatment(id, request));
    }
}
