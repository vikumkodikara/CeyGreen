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

    @Column(nullable = false)
    private boolean active = true;

}
