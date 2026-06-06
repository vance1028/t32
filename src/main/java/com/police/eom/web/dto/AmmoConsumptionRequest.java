package com.police.eom.web.dto;

public class AmmoConsumptionRequest {
    private Long issuanceId;
    private int consumedQuantity;
    private String consumptionType;
    private String reason;
    private Long recordedBy;

    public Long getIssuanceId() { return issuanceId; }
    public void setIssuanceId(Long issuanceId) { this.issuanceId = issuanceId; }
    public int getConsumedQuantity() { return consumedQuantity; }
    public void setConsumedQuantity(int consumedQuantity) { this.consumedQuantity = consumedQuantity; }
    public String getConsumptionType() { return consumptionType; }
    public void setConsumptionType(String consumptionType) { this.consumptionType = consumptionType; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Long getRecordedBy() { return recordedBy; }
    public void setRecordedBy(Long recordedBy) { this.recordedBy = recordedBy; }
}
