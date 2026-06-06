package com.police.eom.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ammo_inventory_checks")
public class AmmoInventoryCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "check_date", nullable = false)
    private LocalDate checkDate;

    @Column(name = "book_quantity", nullable = false)
    private int bookQuantity;

    @Column(name = "actual_quantity", nullable = false)
    private int actualQuantity;

    @Column(nullable = false)
    private int difference = 0;

    @Column(name = "check_status", nullable = false, length = 16)
    private String checkStatus = "PENDING";

    @Column(nullable = false, length = 500)
    private String reason = "";

    @Column(name = "checked_by")
    private Long checkedBy;

    @Column(name = "adjusted_by")
    private Long adjustedBy;

    @Column(name = "adjusted_at")
    private LocalDateTime adjustedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public LocalDate getCheckDate() { return checkDate; }
    public void setCheckDate(LocalDate checkDate) { this.checkDate = checkDate; }
    public int getBookQuantity() { return bookQuantity; }
    public void setBookQuantity(int bookQuantity) { this.bookQuantity = bookQuantity; }
    public int getActualQuantity() { return actualQuantity; }
    public void setActualQuantity(int actualQuantity) { this.actualQuantity = actualQuantity; }
    public int getDifference() { return difference; }
    public void setDifference(int difference) { this.difference = difference; }
    public String getCheckStatus() { return checkStatus; }
    public void setCheckStatus(String checkStatus) { this.checkStatus = checkStatus; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Long getCheckedBy() { return checkedBy; }
    public void setCheckedBy(Long checkedBy) { this.checkedBy = checkedBy; }
    public Long getAdjustedBy() { return adjustedBy; }
    public void setAdjustedBy(Long adjustedBy) { this.adjustedBy = adjustedBy; }
    public LocalDateTime getAdjustedAt() { return adjustedAt; }
    public void setAdjustedAt(LocalDateTime adjustedAt) { this.adjustedAt = adjustedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
