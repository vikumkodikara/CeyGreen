package com.ceygreen.treatment_service.repository;

import com.ceygreen.treatment_service.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
    List<Treatment> findByDisease_NormalizedNameAndActiveTrue(String normalizedName);

    List<Treatment> findByCropTypeIgnoreCaseAndSeverityIgnoreCaseAndActiveTrue(String cropType, String severity);

    List<Treatment> findByCropTypeIgnoreCaseAndActiveTrue(String cropType);

    List<Treatment> findBySeverityIgnoreCaseAndActiveTrue(String severity);

    List<Treatment> findByActiveTrue();
}