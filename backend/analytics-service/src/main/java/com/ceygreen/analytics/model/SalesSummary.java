package com.ceygreen.analytics.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "sales_summary")
public class SalesSummary {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "farmer_id", nullable = false) private String farmerId;
    @Column(name = "total_revenue", nullable = false) private BigDecimal totalRevenue = BigDecimal.ZERO;
    @Column(name = "total_orders", nullable = false) private int totalOrders = 0;
    @Column(name = "last_updated") private Instant lastUpdated = Instant.now();

    public SalesSummary() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }
}
