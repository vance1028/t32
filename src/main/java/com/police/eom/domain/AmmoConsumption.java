package com.police.eom.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ammo_consumptions")
public class AmmoConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "issuance_id", nullable = false)
    private Long issuanceId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "consumed_quantity", nullable = false)
    private int consumedQuantity;

    @Column(name = "consumption_type", nullable = false, length = 16)
    private String consumptionType;

    @Column(nullable = false, length = 500)
    private String reason = "";

    @Column(name = "recorded_by")
    private Long recordedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIssuanceId() { return issuanceId; }
    public void setIssuanceId(Long issuanceId) { this.issuanceId = issuanceId; }
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public int getConsumedQuantity() { return consumedQuantity; }
    public void setConsumedQuantity(int consumedQuantity) { this.consumedQuantity = consumedQuantity; }
    public String getConsumptionType() { return consumptionType; }
    public void setConsumptionType(String consumptionType) { this.consumptionType = consumptionType; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Long getRecordedBy() { return recordedBy; }
    public void setRecordedBy(Long recordedBy) { this.recordedBy = recordedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
