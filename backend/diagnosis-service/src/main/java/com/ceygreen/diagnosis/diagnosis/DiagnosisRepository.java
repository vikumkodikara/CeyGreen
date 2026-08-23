package com.ceygreen.diagnosis.diagnosis;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DiagnosisRepository extends MongoRepository<Diagnosis, String> {

    List<Diagnosis> findByFarmerIdOrderByTimestampDesc(UUID farmerId);

    Page<Diagnosis> findByFarmerIdOrderByTimestampDesc(UUID farmerId, Pageable pageable);

    Optional<Diagnosis> findByImageHashAndFarmerId(String imageHash, UUID farmerId);
}

