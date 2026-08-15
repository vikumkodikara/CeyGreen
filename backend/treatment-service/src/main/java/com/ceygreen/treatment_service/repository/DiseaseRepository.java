package com.ceygreen.treatment_service.repository;

import com.ceygreen.treatment_service.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    Optional<Disease> findByNormalizedName(String normalizedName);
}