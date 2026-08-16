package com.ceygreen.treatment_service.repository;

import com.ceygreen.treatment_service.entity.TreatmentRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentRatingRepository extends JpaRepository<TreatmentRating, Long> {
    List<TreatmentRating> findByTreatmentId(Long treatmentId);
    boolean existsByTreatmentIdAndFarmerId(Long treatmentId, String farmerId);
}
