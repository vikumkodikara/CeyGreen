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
import com.ceygreen.treatment_service.dto.RatingResponse;
import com.ceygreen.treatment_service.util.DiseaseNameUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor

public class TreatmentService {
    private final TreatmentRepository treatmentRepository;
    private final DiseaseRepository diseaseRepository;
    private final TreatmentRatingRepository ratingRepository;
    private final TreatmentEventProducer eventProducer;

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
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

    @Transactional
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
                .addedByFarmerId(request.addedByFarmerId())
                .addedByFarmerName(request.addedByFarmerName())
                .build();

        return toResponse(treatmentRepository.save(treatment));
    }

    @Transactional
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

    @Transactional
    public void rateTreatment(Long id, RatingRequest request) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found: " + id));

        TreatmentRating rating = ratingRepository.findByTreatmentIdAndFarmerId(id, request.farmerId())
                .orElse(TreatmentRating.builder()
                        .treatment(treatment)
                        .farmerId(request.farmerId())
                        .build());

        rating.setRating(request.rating());
        rating.setFarmerName(request.farmerName());
        rating.setComment(request.comment());
        
        ratingRepository.save(rating);
    }

    @Transactional(readOnly = true)
    public List<TreatmentResponse> getAlternatives(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found: " + id));
        List<Treatment> alternatives = treatmentRepository.findByDiseaseIdAndIdNotAndActiveTrue(
                treatment.getDisease().getId(), id);

        if (alternatives.isEmpty() && treatment.getCropType() != null) {
            alternatives = treatmentRepository.findByCropTypeIgnoreCaseAndActiveTrue(treatment.getCropType())
                    .stream()
                    .filter(t -> !t.getId().equals(id))
                    .toList();
        }

        return alternatives.stream().map(this::toResponse).toList();
    }

    @Transactional
    public void deleteTreatment(Long id, String farmerId) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found: " + id));

        if (treatment.getAddedByFarmerId() == null || !treatment.getAddedByFarmerId().equals(farmerId)) {
            throw new RuntimeException("Unauthorized: You can only delete treatments you added.");
        }

        // Before deleting treatment, we must delete associated ratings to avoid foreign key constraints
        List<TreatmentRating> ratings = ratingRepository.findByTreatmentId(id);
        ratingRepository.deleteAll(ratings);

        treatmentRepository.delete(treatment);
    }

    private TreatmentResponse toResponse(Treatment t) {
        List<TreatmentRating> ratings = ratingRepository.findByTreatmentId(t.getId());
        Double avgRating = ratings.isEmpty() ? null : 
                ratings.stream().mapToInt(TreatmentRating::getRating).average().orElse(0.0);

        List<RatingResponse> reviewResponses = ratings.stream()
                .map(r -> new RatingResponse(r.getFarmerId(), r.getFarmerName(), r.getRating(), r.getComment(), r.getCreatedAt()))
                .toList();

        return new TreatmentResponse(
                t.getId(), t.getDisease().getName(), t.getProductName(), t.getType(),
                t.getDosage(), t.getFrequency(), t.getSafetyNotes(), t.getCropType(),
                t.getSeverity(), t.getPhiDays(), t.getApplicationMethod(), t.getBrandNames(),
                t.getEffectivenessScore(), avgRating, reviewResponses, t.isActive(),
                t.getAddedByFarmerId(), t.getAddedByFarmerName());
    }

}
