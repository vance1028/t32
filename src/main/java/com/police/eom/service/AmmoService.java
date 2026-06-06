package com.police.eom.service;

import com.police.eom.domain.*;
import com.police.eom.repo.*;
import com.police.eom.web.ApiException;
import com.police.eom.web.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AmmoService {

    private final AmmoBatchRepository batchRepo;
    private final AmmoTransactionRepository transactionRepo;
    private final AmmoConsumptionRepository consumptionRepo;
    private final AmmoInventoryCheckRepository inventoryCheckRepo;
    private final ShootingRecordRepository shootingRecordRepo;
    private final FirearmIssuanceRepository issuanceRepo;
    private final OfficerRepository officerRepo;

    public AmmoService(AmmoBatchRepository batchRepo,
                       AmmoTransactionRepository transactionRepo,
                       AmmoConsumptionRepository consumptionRepo,
                       AmmoInventoryCheckRepository inventoryCheckRepo,
                       ShootingRecordRepository shootingRecordRepo,
                       FirearmIssuanceRepository issuanceRepo,
                       OfficerRepository officerRepo) {
        this.batchRepo = batchRepo;
        this.transactionRepo = transactionRepo;
        this.consumptionRepo = consumptionRepo;
        this.inventoryCheckRepo = inventoryCheckRepo;
        this.shootingRecordRepo = shootingRecordRepo;
        this.issuanceRepo = issuanceRepo;
        this.officerRepo = officerRepo;
    }

    public List<AmmoBatch> listBatches(String status, String caliber) {
        if (status != null && !status.isBlank()) return batchRepo.findByStatus(status);
        if (caliber != null && !caliber.isBlank()) return batchRepo.findByCaliber(caliber);
        return batchRepo.findAll();
    }

    public AmmoBatch getBatch(Long id) {
        return batchRepo.findById(id)
                .orElseThrow(() -> ApiException.notFound("弹药批次不存在"));
    }

    @Transactional
    public AmmoBatch createBatch(AmmoBatch input) {
        if (input.getBatchNo() == null || input.getBatchNo().isBlank()) {
            throw ApiException.badRequest("批次号不能为空");
        }
        if (input.getCaliber() == null || input.getCaliber().isBlank()) {
            throw ApiException.badRequest("口径不能为空");
        }
        if (input.getModel() == null || input.getModel().isBlank()) {
            throw ApiException.badRequest("型号不能为空");
        }
        if (batchRepo.existsByBatchNo(input.getBatchNo())) {
            throw ApiException.conflict("批次号已存在");
        }
        input.setId(null);
        input.setCurrentQuantity(0);
        input.setInitialQuantity(0);
        input.setStatus("ACTIVE");
        return batchRepo.save(input);
    }

    @Transactional
    public AmmoBatch inbound(AmmoInboundRequest req) {
        if (req.getQuantity() <= 0) {
            throw ApiException.badRequest("入库数量必须大于0");
        }
        if (req.getBatchNo() == null || req.getBatchNo().isBlank()) {
            throw ApiException.badRequest("批次号不能为空");
        }

        AmmoBatch batch = batchRepo.findByBatchNo(req.getBatchNo()).orElse(null);
        if (batch == null) {
            batch = new AmmoBatch();
            batch.setBatchNo(req.getBatchNo());
            batch.setCaliber(req.getCaliber());
            batch.setModel(req.getModel());
            batch.setManufacturer(req.getManufacturer() != null ? req.getManufacturer() : "");
            batch.setProductionDate(req.getProductionDate());
            batch.setExpiryDate(req.getExpiryDate());
            batch.setUnitPrice(req.getUnitPrice());
            batch.setInitialQuantity(req.getQuantity());
            batch.setCurrentQuantity(req.getQuantity());
            batch.setStatus("ACTIVE");
            batch.setRemark(req.getRemark() != null ? req.getRemark() : "");
            batch = batchRepo.save(batch);
        } else {
            if (!"ACTIVE".equals(batch.getStatus())) {
                throw ApiException.conflict("批次状态异常，无法入库");
            }
            batch = batchRepo.findByIdWithLock(batch.getId())
                    .orElseThrow(() -> ApiException.notFound("弹药批次不存在"));
            batch.setCurrentQuantity(batch.getCurrentQuantity() + req.getQuantity());
            batch = batchRepo.save(batch);
        }

        AmmoTransaction tx = new AmmoTransaction();
        tx.setBatchId(batch.getId());
        tx.setTransactionType("IN");
        tx.setQuantity(req.getQuantity());
        tx.setBalanceAfter(batch.getCurrentQuantity());
        tx.setReferenceType("PURCHASE");
        tx.setOperatorId(req.getOperatorId());
        tx.setRemark(req.getRemark() != null ? req.getRemark() : "弹药入库");
        transactionRepo.save(tx);

        return batch;
    }

    @Transactional
    public AmmoBatch outbound(AmmoOutboundRequest req) {
        if (req.getQuantity() <= 0) {
            throw ApiException.badRequest("出库数量必须大于0");
        }

        AmmoBatch batch = batchRepo.findByIdWithLock(req.getBatchId())
                .orElseThrow(() -> ApiException.notFound("弹药批次不存在"));

        if (!"ACTIVE".equals(batch.getStatus())) {
            throw ApiException.conflict("批次状态异常，无法出库");
        }
        if (batch.getCurrentQuantity() < req.getQuantity()) {
            throw ApiException.conflict("库存不足，当前库存：" + batch.getCurrentQuantity());
        }

        batch.setCurrentQuantity(batch.getCurrentQuantity() - req.getQuantity());
        batchRepo.save(batch);

        AmmoTransaction tx = new AmmoTransaction();
        tx.setBatchId(batch.getId());
        tx.setTransactionType("OUT");
        tx.setQuantity(req.getQuantity());
        tx.setBalanceAfter(batch.getCurrentQuantity());
        tx.setReferenceType("MANUAL_OUT");
        tx.setOperatorId(req.getOperatorId());
        tx.setRemark(req.getRemark() != null ? req.getRemark() : "弹药出库");
        transactionRepo.save(tx);

        return batch;
    }

    @Transactional
    public AmmoBatch scrap(AmmoScrapRequest req) {
        if (req.getQuantity() <= 0) {
            throw ApiException.badRequest("报废数量必须大于0");
        }

        AmmoBatch batch = batchRepo.findByIdWithLock(req.getBatchId())
                .orElseThrow(() -> ApiException.notFound("弹药批次不存在"));

        if (!"ACTIVE".equals(batch.getStatus())) {
            throw ApiException.conflict("批次状态异常，无法报废");
        }
        if (batch.getCurrentQuantity() < req.getQuantity()) {
            throw ApiException.conflict("库存不足，当前库存：" + batch.getCurrentQuantity());
        }

        batch.setCurrentQuantity(batch.getCurrentQuantity() - req.getQuantity());
        batchRepo.save(batch);

        AmmoTransaction tx = new AmmoTransaction();
        tx.setBatchId(batch.getId());
        tx.setTransactionType("SCRAP");
        tx.setQuantity(req.getQuantity());
        tx.setBalanceAfter(batch.getCurrentQuantity());
        tx.setReferenceType("SCRAP");
        tx.setOperatorId(req.getOperatorId());
        tx.setRemark("报废：" + (req.getReason() != null ? req.getReason() : ""));
        transactionRepo.save(tx);

        return batch;
    }

    @Transactional
    public void issueAmmo(Long issuanceId, Long batchId, int quantity, Long operatorId) {
        if (quantity <= 0) {
            throw ApiException.badRequest("发放数量必须大于0");
        }

        AmmoBatch batch = batchRepo.findByIdWithLock(batchId)
                .orElseThrow(() -> ApiException.notFound("弹药批次不存在"));

        if (!"ACTIVE".equals(batch.getStatus())) {
            throw ApiException.conflict("批次状态异常，无法发放");
        }
        if (batch.getCurrentQuantity() < quantity) {
            throw ApiException.conflict("库存不足，当前库存：" + batch.getCurrentQuantity());
        }

        batch.setCurrentQuantity(batch.getCurrentQuantity() - quantity);
        batchRepo.save(batch);

        AmmoTransaction tx = new AmmoTransaction();
        tx.setBatchId(batchId);
        tx.setTransactionType("OUT");
        tx.setQuantity(quantity);
        tx.setBalanceAfter(batch.getCurrentQuantity());
        tx.setReferenceType("ISSUANCE");
        tx.setReferenceId(issuanceId);
        tx.setOperatorId(operatorId);
        tx.setRemark("枪械领用发放");
        transactionRepo.save(tx);
    }

    @Transactional
    public void returnAmmo(Long issuanceId, Long batchId, int quantity, Long operatorId) {
        if (quantity <= 0) {
            throw ApiException.badRequest("归还数量必须大于0");
        }

        AmmoBatch batch = batchRepo.findByIdWithLock(batchId)
                .orElseThrow(() -> ApiException.notFound("弹药批次不存在"));

        if (!"ACTIVE".equals(batch.getStatus())) {
            throw ApiException.conflict("批次状态异常，无法归还");
        }

        batch.setCurrentQuantity(batch.getCurrentQuantity() + quantity);
        batchRepo.save(batch);

        AmmoTransaction tx = new AmmoTransaction();
        tx.setBatchId(batchId);
        tx.setTransactionType("RETURN");
        tx.setQuantity(quantity);
        tx.setBalanceAfter(batch.getCurrentQuantity());
        tx.setReferenceType("ISSUANCE");
        tx.setReferenceId(issuanceId);
        tx.setOperatorId(operatorId);
        tx.setRemark("弹药归还入库");
        transactionRepo.save(tx);
    }

    @Transactional
    public AmmoConsumption recordConsumption(AmmoConsumptionRequest req) {
        if (req.getConsumedQuantity() <= 0) {
            throw ApiException.badRequest("消耗数量必须大于0");
        }
        if (req.getConsumptionType() == null || req.getConsumptionType().isBlank()) {
            throw ApiException.badRequest("消耗类型不能为空");
        }
        if (!List.of("TRAINING", "COMBAT", "LOST").contains(req.getConsumptionType())) {
            throw ApiException.badRequest("消耗类型必须是 TRAINING、COMBAT 或 LOST");
        }

        FirearmIssuance issuance = issuanceRepo.findById(req.getIssuanceId())
                .orElseThrow(() -> ApiException.notFound("领用记录不存在"));

        if (issuance.getAmmoBatchId() == null) {
            throw ApiException.conflict("该领用记录未关联弹药批次");
        }

        int totalConsumed = consumptionRepo.sumConsumedByIssuanceId(req.getIssuanceId());
        int available = issuance.getAmmoIssued() - totalConsumed;
        if (req.getConsumedQuantity() > available) {
            throw ApiException.conflict("消耗数量超过可消耗额度，剩余可消耗：" + available);
        }

        AmmoConsumption consumption = new AmmoConsumption();
        consumption.setIssuanceId(req.getIssuanceId());
        consumption.setBatchId(issuance.getAmmoBatchId());
        consumption.setConsumedQuantity(req.getConsumedQuantity());
        consumption.setConsumptionType(req.getConsumptionType());
        consumption.setReason(req.getReason() != null ? req.getReason() : "");
        consumption.setRecordedBy(req.getRecordedBy());
        consumption = consumptionRepo.save(consumption);

        issuance.setAmmoConsumed(totalConsumed + req.getConsumedQuantity());
        issuanceRepo.save(issuance);

        return consumption;
    }

    public void validateReconciliation(Long issuanceId) {
        FirearmIssuance issuance = issuanceRepo.findById(issuanceId)
                .orElseThrow(() -> ApiException.notFound("领用记录不存在"));

        int issued = issuance.getAmmoIssued();
        int returned = issuance.getAmmoReturned() != null ? issuance.getAmmoReturned() : 0;
        int consumed = consumptionRepo.sumConsumedByIssuanceId(issuanceId);

        if (issued != returned + consumed) {
            throw ApiException.conflict(
                String.format("数量不守恒：发放%d发 = 归还%d发 + 消耗%d发，差值%d发",
                    issued, returned, consumed, issued - returned - consumed)
            );
        }
    }

    @Transactional
    public void closeIssuance(Long issuanceId) {
        FirearmIssuance issuance = issuanceRepo.findById(issuanceId)
                .orElseThrow(() -> ApiException.notFound("领用记录不存在"));

        if (!"RETURNED".equals(issuance.getStatus())) {
            throw ApiException.conflict("枪械未归还，无法结案");
        }

        int issued = issuance.getAmmoIssued();
        int returned = issuance.getAmmoReturned() != null ? issuance.getAmmoReturned() : 0;
        int consumed = consumptionRepo.sumConsumedByIssuanceId(issuanceId);

        if (issued == returned + consumed) {
            issuance.setReconciliationStatus("VERIFIED");
        } else {
            issuance.setReconciliationStatus("DISCREPANCY");
        }
        issuanceRepo.save(issuance);

        if ("DISCREPANCY".equals(issuance.getReconciliationStatus())) {
            throw ApiException.conflict(
                String.format("数量不守恒，挂为待核查：发放%d发 = 归还%d发 + 消耗%d发，差值%d发",
                    issued, returned, consumed, issued - returned - consumed)
            );
        }
    }

    public AmmoReconciliationResult reconcileBatch(Long batchId) {
        AmmoBatch batch = getBatch(batchId);
        int calculated = transactionRepo.calculateBalanceByBatchId(batchId);
        return new AmmoReconciliationResult(batch.getId(), batch.getBatchNo(),
                batch.getCurrentQuantity(), calculated);
    }

    public List<AmmoTransaction> getTransactionsByBatch(Long batchId) {
        getBatch(batchId);
        return transactionRepo.findByBatchIdOrderByOccurredAtDesc(batchId);
    }

    public List<AmmoConsumption> getConsumptionsByIssuance(Long issuanceId) {
        if (!issuanceRepo.existsById(issuanceId)) {
            throw ApiException.notFound("领用记录不存在");
        }
        return consumptionRepo.findByIssuanceId(issuanceId);
    }

    @Transactional
    public AmmoInventoryCheck createInventoryCheck(InventoryCheckRequest req) {
        AmmoBatch batch = getBatch(req.getBatchId());

        AmmoInventoryCheck check = new AmmoInventoryCheck();
        check.setBatchId(req.getBatchId());
        check.setCheckDate(req.getCheckDate() != null ? req.getCheckDate() : LocalDate.now());
        check.setBookQuantity(batch.getCurrentQuantity());
        check.setActualQuantity(req.getActualQuantity());
        check.setDifference(req.getActualQuantity() - batch.getCurrentQuantity());
        check.setCheckStatus("PENDING");
        check.setCheckedBy(req.getCheckedBy());
        return inventoryCheckRepo.save(check);
    }

    @Transactional
    public AmmoInventoryCheck adjustInventory(Long checkId, InventoryAdjustRequest req) {
        AmmoInventoryCheck check = inventoryCheckRepo.findById(checkId)
                .orElseThrow(() -> ApiException.notFound("盘点记录不存在"));

        if (!"PENDING".equals(check.getCheckStatus())) {
            throw ApiException.conflict("该盘点已处理，无法重复调整");
        }
        if (req.getReason() == null || req.getReason().isBlank()) {
            throw ApiException.badRequest("必须说明盈亏原因");
        }

        AmmoBatch batch = batchRepo.findByIdWithLock(check.getBatchId())
                .orElseThrow(() -> ApiException.notFound("弹药批次不存在"));

        int diff = check.getDifference();
        String txType;
        if (diff > 0) {
            txType = "ADJUST_PLUS";
            batch.setCurrentQuantity(batch.getCurrentQuantity() + diff);
        } else if (diff < 0) {
            txType = "ADJUST_MINUS";
            batch.setCurrentQuantity(batch.getCurrentQuantity() + diff);
        } else {
            check.setCheckStatus("ADJUSTED");
            check.setReason(req.getReason());
            check.setAdjustedBy(req.getAdjustedBy());
            check.setAdjustedAt(LocalDateTime.now());
            return inventoryCheckRepo.save(check);
        }

        if (batch.getCurrentQuantity() < 0) {
            throw ApiException.conflict("调整后库存不能为负");
        }

        batchRepo.save(batch);

        AmmoTransaction tx = new AmmoTransaction();
        tx.setBatchId(batch.getId());
        tx.setTransactionType(txType);
        tx.setQuantity(Math.abs(diff));
        tx.setBalanceAfter(batch.getCurrentQuantity());
        tx.setReferenceType("INVENTORY_CHECK");
        tx.setReferenceId(checkId);
        tx.setOperatorId(req.getAdjustedBy());
        tx.setRemark("盘点调整：" + req.getReason());
        transactionRepo.save(tx);

        check.setCheckStatus("ADJUSTED");
        check.setReason(req.getReason());
        check.setAdjustedBy(req.getAdjustedBy());
        check.setAdjustedAt(LocalDateTime.now());
        return inventoryCheckRepo.save(check);
    }

    public List<AmmoInventoryCheck> getInventoryChecksByBatch(Long batchId) {
        getBatch(batchId);
        return inventoryCheckRepo.findByBatchIdOrderByCheckDateDesc(batchId);
    }

    @Transactional
    public ShootingRecord recordShooting(ShootingRecordRequest req) {
        if (req.getShotsFired() <= 0) {
            throw ApiException.badRequest("射击发数必须大于0");
        }

        if (!officerRepo.existsById(req.getOfficerId())) {
            throw ApiException.badRequest("射击民警不存在");
        }

        AmmoBatch batch = getBatch(req.getBatchId());

        ShootingRecord record = new ShootingRecord();
        record.setIssuanceId(req.getIssuanceId());
        record.setOfficerId(req.getOfficerId());
        record.setFirearmId(req.getFirearmId());
        record.setBatchId(req.getBatchId());
        record.setShotsFired(req.getShotsFired());
        record.setShootingPurpose(req.getShootingPurpose());
        record.setLocation(req.getLocation() != null ? req.getLocation() : "");
        record.setOccurredAt(req.getOccurredAt() != null ? req.getOccurredAt() : LocalDateTime.now());
        record.setRecordedBy(req.getRecordedBy());
        record.setRemark(req.getRemark() != null ? req.getRemark() : "");
        return shootingRecordRepo.save(record);
    }

    public List<ShootingRecord> getShootingRecordsByIssuance(Long issuanceId) {
        if (!issuanceRepo.existsById(issuanceId)) {
            throw ApiException.notFound("领用记录不存在");
        }
        return shootingRecordRepo.findByIssuanceId(issuanceId);
    }

    public List<ShootingRecord> getShootingRecordsByOfficer(Long officerId) {
        if (!officerRepo.existsById(officerId)) {
            throw ApiException.notFound("民警不存在");
        }
        return shootingRecordRepo.findByOfficerId(officerId);
    }
}
