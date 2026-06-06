package com.police.eom.web.dto;

import java.time.LocalDateTime;

public class ShootingRecordRequest {
    private Long issuanceId;
    private Long officerId;
    private Long firearmId;
    private Long batchId;
    private int shotsFired;
    private String shootingPurpose;
    private String location;
    private LocalDateTime occurredAt;
    private Long recordedBy;
    private String remark;

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
}
