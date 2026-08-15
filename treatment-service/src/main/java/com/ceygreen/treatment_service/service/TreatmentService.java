package com.ceygreen.treatment_service.service;

import com.ceygreen.treatment_service.dto.TreatmentRequest;
import com.ceygreen.treatment_service.dto.TreatmentResponse;
import com.ceygreen.treatment_service.entity.Disease;
import com.ceygreen.treatment_service.entity.Treatment;
import com.ceygreen.treatment_service.exception.ResourceNotFoundException;
import com.ceygreen.treatment_service.kafka.TreatmentEvent;
import com.ceygreen.treatment_service.kafka.TreatmentEventProducer;
import com.ceygreen.treatment_service.repository.DiseaseRepository;
import com.ceygreen.treatment_service.repository.TreatmentRepository;
import com.ceygreen.treatment_service.util.DiseaseNameUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor

public class TreatmentService {
    private final TreatmentRepository treatmentRepository;
    private final DiseaseRepository diseaseRepository;
    private final TreatmentEventProducer eventProducer;

    public List<TreatmentResponse> getTreatmentsByDisease(String diseaseName) {
        String normalized = DiseaseNameUtil.normalize(diseaseName);
        List<Treatment> treatments = treatmentRepository.findByDisease_NormalizedNameAndActiveTrue(normalized);
        if (treatments.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No treatments found for disease: '" + diseaseName +
                            "' (normalized: '" + normalized + "').");
        }
        treatments.stream()
                .filter(t -> "SEVERE".equalsIgnoreCase(t.getSeverity()))
                .forEach(t -> eventProducer.publish(new TreatmentEvent(
                        diseaseName, t.getId(), t.getProductName(), t.getSeverity(), Instant.now())));
        return treatments.stream().map(this::toResponse).toList();
    }

    public List<TreatmentResponse> searchTreatments(String crop, String severity) {
        List<Treatment> results;
        if (crop != null && severity != null) {
            results = treatmentRepository.findByCropTypeIgnoreCaseAndSeverityIgnoreCaseAndActiveTrue(crop, severity);
        } else if (crop != null) {
            results = treatmentRepository.findByCropTypeIgnoreCaseAndActiveTrue(crop);
        } else if (severity != null) {
            results = treatmentRepository.findBySeverityIgnoreCaseAndActiveTrue(severity);
        } else {
            results = treatmentRepository.findByActiveTrue();
        }
        return results.stream().map(this::toResponse).toList();
    }

    public TreatmentResponse createTreatment(TreatmentRequest request) {
        String normalized = DiseaseNameUtil.normalize(request.diseaseName());
        Disease disease = diseaseRepository.findByNormalizedName(normalized)
                .orElseThrow(() -> new ResourceNotFoundException("Disease not found: " + request.diseaseName()));

        Treatment treatment = Treatment.builder()
                .disease(disease)
                .productName(request.productName())
                .type(request.type())
                .dosage(request.dosage())
                .frequency(request.frequency())
                .safetyNotes(request.safetyNotes())
                .cropType(request.cropType())
                .severity(request.severity())
                .active(request.active())
                .build();

        return toResponse(treatmentRepository.save(treatment));
    }

    public TreatmentResponse updateTreatment(Long id, TreatmentRequest request) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found: " + id));

        treatment.setDosage(request.dosage());
        treatment.setFrequency(request.frequency());
        treatment.setSafetyNotes(request.safetyNotes());
        treatment.setSeverity(request.severity());
        if (request.active() != null) {
            treatment.setActive(request.active());
        }

        return toResponse(treatmentRepository.save(treatment));
    }

    private TreatmentResponse toResponse(Treatment t) {
        return new TreatmentResponse(
                t.getId(), t.getDisease().getName(), t.getProductName(), t.getType(),
                t.getDosage(), t.getFrequency(), t.getSafetyNotes(), t.getCropType(),
                t.getSeverity(), t.isActive());
    }

}
