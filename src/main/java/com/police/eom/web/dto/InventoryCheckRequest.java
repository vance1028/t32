package com.police.eom.web.dto;

import java.time.LocalDate;

public class InventoryCheckRequest {
    private Long batchId;
    private LocalDate checkDate;
    private int actualQuantity;
    private Long checkedBy;

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public LocalDate getCheckDate() { return checkDate; }
    public void setCheckDate(LocalDate checkDate) { this.checkDate = checkDate; }
    public int getActualQuantity() { return actualQuantity; }
    public void setActualQuantity(int actualQuantity) { this.actualQuantity = actualQuantity; }
    public Long getCheckedBy() { return checkedBy; }
    public void setCheckedBy(Long checkedBy) { this.checkedBy = checkedBy; }
}
