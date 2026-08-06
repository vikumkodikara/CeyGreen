package com.ceygreen.treatment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "treatments")
public class Treatment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id", nullable = false)
    @JsonIgnore
    private Disease disease;

    @Column(name = "product_name", nullable = false)
    private String productName;

    /** CHEMICAL or ORGANIC */
    @Column(nullable = false)
    private String type;

    private String dosage;
    private String frequency;

    @Column(name = "safety_notes")
    private String safetyNotes;

    @Column(nullable = false)
    private boolean active = true;

    public Treatment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Disease getDisease() { return disease; }
    public void setDisease(Disease disease) { this.disease = disease; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public String getSafetyNotes() { return safetyNotes; }
    public void setSafetyNotes(String safetyNotes) { this.safetyNotes = safetyNotes; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
