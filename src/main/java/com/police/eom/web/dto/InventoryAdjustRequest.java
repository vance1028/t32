package com.police.eom.web.dto;

public class InventoryAdjustRequest {
    private String reason;
    private Long adjustedBy;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Long getAdjustedBy() { return adjustedBy; }
    public void setAdjustedBy(Long adjustedBy) { this.adjustedBy = adjustedBy; }
}
