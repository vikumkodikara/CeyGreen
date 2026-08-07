package com.ceygreen.treatment.repository;

import com.ceygreen.treatment.model.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TreatmentRepository extends JpaRepository<Treatment, Long> {

    List<Treatment> findByDiseaseNameIgnoreCaseAndActiveTrue(String diseaseName);

    @Query("SELECT t FROM Treatment t JOIN t.disease d WHERE "
            + "(:crop IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :crop, '%'))) "
            + "AND (:type IS NULL OR t.type = :type) "
            + "AND t.active = true")
    List<Treatment> searchTreatments(@Param("crop") String crop, @Param("type") String type);
}
