package com.ceygreen.analytics.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "order_log")
public class OrderLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "farmer_id", nullable = false)
    private String farmerId;

    @Column(name = "buyer_id")
    private String buyerId;

    @Column(name = "product_id")
    private Long productId;

    // Dual-mapped / synced columns for Grafana and API compatibility
    @Column(name = "product")
    private String product;

    @Column(name = "crop_name")
    private String cropName;

    @Column(name = "quantity")
    private Integer quantity = 1;

    @Column(name = "unit_price")
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "amount", precision = 14, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 14, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "recorded_at")
    private Instant recordedAt = Instant.now();

    @Column(name = "received_at")
    private Instant receivedAt = Instant.now();

    public OrderLog() {}

    public OrderLog(Long orderId, String farmerId, String buyerId, Long productId,
                    String cropName, Integer quantity, BigDecimal unitPrice,
                    BigDecimal totalAmount, Instant receivedAt) {
        this.orderId = orderId;
        this.farmerId = farmerId;
        this.buyerId = buyerId;
        this.productId = productId;
        this.cropName = cropName != null ? cropName : "Produce";
        this.product = this.cropName;
        this.quantity = quantity != null ? quantity : 1;
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        this.amount = this.totalAmount;
        this.receivedAt = receivedAt != null ? receivedAt : Instant.now();
        this.recordedAt = this.receivedAt;
    }

    @PrePersist
    @PreUpdate
    public void syncFields() {
        if (this.product == null) this.product = this.cropName != null ? this.cropName : "Produce";
        if (this.cropName == null) this.cropName = this.product;
        if (this.amount == null) this.amount = this.totalAmount != null ? this.totalAmount : BigDecimal.ZERO;
        if (this.totalAmount == null) this.totalAmount = this.amount;
        if (this.recordedAt == null) this.recordedAt = this.receivedAt != null ? this.receivedAt : Instant.now();
        if (this.receivedAt == null) this.receivedAt = this.recordedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getBuyerId() { return buyerId; }
    public void setBuyerId(String buyerId) { this.buyerId = buyerId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProduct() { return product != null ? product : cropName; }
    public void setProduct(String product) { 
        this.product = product;
        if (this.cropName == null) this.cropName = product;
    }

    public String getCropName() { return cropName != null ? cropName : product; }
    public void setCropName(String cropName) { 
        this.cropName = cropName;
        if (this.product == null) this.product = cropName;
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public BigDecimal getAmount() { return amount != null ? amount : totalAmount; }
    public void setAmount(BigDecimal amount) { 
        this.amount = amount;
        if (this.totalAmount == null) this.totalAmount = amount;
    }

    public BigDecimal getTotalAmount() { return totalAmount != null ? totalAmount : amount; }
    public void setTotalAmount(BigDecimal totalAmount) { 
        this.totalAmount = totalAmount;
        if (this.amount == null) this.amount = totalAmount;
    }

    public Instant getRecordedAt() { return recordedAt != null ? recordedAt : receivedAt; }
    public void setRecordedAt(Instant recordedAt) { 
        this.recordedAt = recordedAt;
        if (this.receivedAt == null) this.receivedAt = recordedAt;
    }

    public Instant getReceivedAt() { return receivedAt != null ? receivedAt : recordedAt; }
    public void setReceivedAt(Instant receivedAt) { 
        this.receivedAt = receivedAt;
        if (this.recordedAt == null) this.recordedAt = receivedAt;
    }
}
