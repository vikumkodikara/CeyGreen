package com.ceygreen.treatment_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "treatments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Treatment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id", nullable = false)
    private Disease disease;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(nullable = false)
    private String type; // ORGANIC or CHEMICAL

    private String dosage;
    private String frequency;

    @Column(name = "safety_notes")
    private String safetyNotes;

    @Column(name = "crop_type")
    private String cropType;

    private String severity; // MILD, MODERATE, SEVERE

    @Column(name = "phi_days")
    private Integer phiDays; // Pre-Harvest Interval

    @Column(name = "application_method")
    private String applicationMethod;

    @Column(name = "brand_names")
    private String brandNames;

    @Column(name = "effectiveness_score")
    private Integer effectivenessScore;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "added_by_farmer_id")
    private String addedByFarmerId;

    @Column(name = "added_by_farmer_name")
    private String addedByFarmerName;

    // Optional relation for ratings if needed
    @OneToMany(mappedBy = "treatment", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<TreatmentRating> ratings = new java.util.ArrayList<>();

}
