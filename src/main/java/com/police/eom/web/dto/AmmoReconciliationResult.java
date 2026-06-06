package com.police.eom.web.dto;

public class AmmoReconciliationResult {
    private Long batchId;
    private String batchNo;
    private int bookQuantity;
    private int calculatedQuantity;
    private int difference;
    private boolean matched;
    private String message;

    public AmmoReconciliationResult() {}

    public AmmoReconciliationResult(Long batchId, String batchNo, int bookQuantity, int calculatedQuantity) {
        this.batchId = batchId;
        this.batchNo = batchNo;
        this.bookQuantity = bookQuantity;
        this.calculatedQuantity = calculatedQuantity;
        this.difference = bookQuantity - calculatedQuantity;
        this.matched = difference == 0;
        this.message = matched ? "账实一致" : "存在差异，差异数：" + difference;
    }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public int getBookQuantity() { return bookQuantity; }
    public void setBookQuantity(int bookQuantity) { this.bookQuantity = bookQuantity; }
    public int getCalculatedQuantity() { return calculatedQuantity; }
    public void setCalculatedQuantity(int calculatedQuantity) { this.calculatedQuantity = calculatedQuantity; }
    public int getDifference() { return difference; }
    public void setDifference(int difference) { this.difference = difference; }
    public boolean isMatched() { return matched; }
    public void setMatched(boolean matched) { this.matched = matched; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
