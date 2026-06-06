package com.police.eom.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shooting_records")
public class ShootingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "issuance_id")
    private Long issuanceId;

    @Column(name = "officer_id", nullable = false)
    private Long officerId;

    @Column(name = "firearm_id", nullable = false)
    private Long firearmId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "shots_fired", nullable = false)
    private int shotsFired;

    @Column(name = "shooting_purpose", nullable = false, length = 32)
    private String shootingPurpose;

    @Column(nullable = false, length = 128)
    private String location = "";

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "recorded_by")
    private Long recordedBy;

    @Column(nullable = false, length = 500)
    private String remark = "";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (occurredAt == null) occurredAt = LocalDateTime.now();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIssuanceId() { return issuanceId; }
    public void setIssuanceId(Long issuanceId) { this.issuanceId = issuanceId; }
    public Long getOfficerId() { return officerId; }
    public void setOfficerId(Long officerId) { this.officerId = officerId; }
    public Long getFirearmId() { return firearmId; }
    public void setFirearmId(Long firearmId) { this.firearmId = firearmId; }
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public int getShotsFired() { return shotsFired; }
    public void setShotsFired(int shotsFired) { this.shotsFired = shotsFired; }
    public String getShootingPurpose() { return shootingPurpose; }
    public void setShootingPurpose(String shootingPurpose) { this.shootingPurpose = shootingPurpose; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
    public Long getRecordedBy() { return recordedBy; }
    public void setRecordedBy(Long recordedBy) { this.recordedBy = recordedBy; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
