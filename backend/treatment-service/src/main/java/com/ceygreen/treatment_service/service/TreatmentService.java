package com.ceygreen.treatment_service.service;

import com.ceygreen.treatment_service.dto.TreatmentRequest;
import com.ceygreen.treatment_service.dto.TreatmentResponse;
import com.ceygreen.treatment_service.entity.Disease;
import com.ceygreen.treatment_service.entity.Treatment;
import com.ceygreen.treatment_service.exception.ResourceNotFoundException;
import com.ceygreen.treatment_service.kafka.TreatmentEvent;
import com.ceygreen.treatment_service.kafka.TreatmentEventProducer;
import com.ceygreen.treatment_service.repository.DiseaseRepository;
import com.ceygreen.treatment_service.repository.TreatmentRatingRepository;
import com.ceygreen.treatment_service.repository.TreatmentRepository;
import com.ceygreen.treatment_service.entity.TreatmentRating;
import com.ceygreen.treatment_service.dto.RatingRequest;
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
    private final TreatmentRatingRepository ratingRepository;
    private final TreatmentEventProducer eventProducer;

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
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

    public List<TreatmentResponse> searchTreatments(String crop, String severity, String type) {
        List<Treatment> results;
        if (crop != null && severity != null) {
            results = treatmentRepository.findByCropTypeIgnoreCaseAndSeverityIgnoreCaseAndActiveTrue(crop, severity);
        } else if (crop != null) {
            results = treatmentRepository.findByCropTypeIgnoreCaseAndActiveTrue(crop);
        } else if (severity != null) {
            results = treatmentRepository.findBySeverityIgnoreCaseAndActiveTrue(severity);
        } else if (type != null) {
            results = treatmentRepository.findByTypeIgnoreCaseAndActiveTrue(type);
        } else {
            results = treatmentRepository.findByActiveTrue();
        }
        
        if (type != null) {
            results = results.stream().filter(t -> t.getType().equalsIgnoreCase(type)).toList();
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

    public void rateTreatment(Long id, RatingRequest request) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found: " + id));

        if (ratingRepository.existsByTreatmentIdAndFarmerId(id, request.farmerId())) {
            throw new IllegalArgumentException("You have already rated this treatment.");
        }

        TreatmentRating rating = TreatmentRating.builder()
                .treatment(treatment)
                .farmerId(request.farmerId())
                .rating(request.rating())
                .build();
        ratingRepository.save(rating);
    }

    public List<TreatmentResponse> getAlternatives(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found: " + id));
        List<Treatment> alternatives = treatmentRepository.findByDiseaseIdAndIdNotAndActiveTrue(
                treatment.getDisease().getId(), id);
        return alternatives.stream().map(this::toResponse).toList();
    }

    private TreatmentResponse toResponse(Treatment t) {
        List<TreatmentRating> ratings = ratingRepository.findByTreatmentId(t.getId());
        Double avgRating = ratings.isEmpty() ? null : 
                ratings.stream().mapToInt(TreatmentRating::getRating).average().orElse(0.0);

        return new TreatmentResponse(
                t.getId(), t.getDisease().getName(), t.getProductName(), t.getType(),
                t.getDosage(), t.getFrequency(), t.getSafetyNotes(), t.getCropType(),
                t.getSeverity(), t.getPhiDays(), t.getApplicationMethod(), t.getBrandNames(),
                t.getEffectivenessScore(), avgRating, t.isActive());
    }

}
