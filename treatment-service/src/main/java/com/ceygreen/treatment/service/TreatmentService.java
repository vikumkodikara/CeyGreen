package com.ceygreen.treatment.service;

import com.ceygreen.treatment.common.ApiException;
import com.ceygreen.treatment.dto.TreatmentRequest;
import com.ceygreen.treatment.dto.TreatmentResponse;
import com.ceygreen.treatment.dto.TreatmentUpdateRequest;
import com.ceygreen.treatment.kafka.TreatmentEventPublisher;
import com.ceygreen.treatment.model.Disease;
import com.ceygreen.treatment.model.Treatment;
import com.ceygreen.treatment.repository.DiseaseRepository;
import com.ceygreen.treatment.repository.TreatmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TreatmentService {

    private static final Logger log = LoggerFactory.getLogger(TreatmentService.class);

    private final TreatmentRepository treatmentRepository;
    private final DiseaseRepository diseaseRepository;
    private final TreatmentEventPublisher eventPublisher;

    public TreatmentService(TreatmentRepository treatmentRepository,
                            DiseaseRepository diseaseRepository,
                            TreatmentEventPublisher eventPublisher) {
        this.treatmentRepository = treatmentRepository;
        this.diseaseRepository = diseaseRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<TreatmentResponse> getTreatmentsByDisease(String diseaseName) {
        List<Treatment> treatments = treatmentRepository.findByDiseaseNameIgnoreCaseAndActiveTrue(diseaseName);
        if (treatments.isEmpty()) {
            throw ApiException.notFound("No treatments found for disease: " + diseaseName);
        }
        return treatments.stream().map(this::toResponse).toList();
    }

    public List<TreatmentResponse> searchTreatments(String crop, String severity) {
        return treatmentRepository.searchTreatments(crop, severity).stream()
                .map(this::toResponse).toList();
    }

    @Transactional
    public TreatmentResponse createTreatment(TreatmentRequest request) {
        Disease disease = diseaseRepository.findByNameIgnoreCase(request.diseaseName())
                .orElseGet(() -> {
                    Disease d = new Disease(request.diseaseName(), null);
                    return diseaseRepository.save(d);
                });

        Treatment treatment = new Treatment();
        treatment.setDisease(disease);
        treatment.setProductName(request.productName());
        treatment.setType(request.type());
        treatment.setDosage(request.dosage());
        treatment.setFrequency(request.frequency());
        treatment.setSafetyNotes(request.safetyNotes());

        Treatment saved = treatmentRepository.save(treatment);
        log.info("Created treatment id={} for disease={}", saved.getId(), request.diseaseName());
        return toResponse(saved);
    }

    @Transactional
    public TreatmentResponse updateTreatment(Long id, TreatmentUpdateRequest request) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Treatment not found: " + id));

        if (request.productName() != null) treatment.setProductName(request.productName());
        if (request.type() != null) treatment.setType(request.type());
        if (request.dosage() != null) treatment.setDosage(request.dosage());
        if (request.frequency() != null) treatment.setFrequency(request.frequency());
        if (request.safetyNotes() != null) treatment.setSafetyNotes(request.safetyNotes());
        if (request.active() != null) treatment.setActive(request.active());

        Treatment saved = treatmentRepository.save(treatment);
        log.info("Updated treatment id={}", id);
        return toResponse(saved);
    }

    private TreatmentResponse toResponse(Treatment t) {
        return new TreatmentResponse(
                t.getId(),
                t.getDisease().getName(),
                t.getProductName(),
                t.getType(),
                t.getDosage(),
                t.getFrequency(),
                t.getSafetyNotes(),
                t.isActive()
        );
    }
}
