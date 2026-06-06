package com.police.eom.web;

import com.police.eom.domain.*;
import com.police.eom.service.AmmoService;
import com.police.eom.web.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ammo")
public class AmmoController {

    private final AmmoService ammoService;

    public AmmoController(AmmoService ammoService) {
        this.ammoService = ammoService;
    }

    @GetMapping("/batches")
    public List<AmmoBatch> listBatches(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String caliber) {
        return ammoService.listBatches(status, caliber);
    }

    @GetMapping("/batches/{id}")
    public AmmoBatch getBatch(@PathVariable Long id) {
        return ammoService.getBatch(id);
    }

    @PostMapping("/batches")
    public ResponseEntity<AmmoBatch> createBatch(@RequestBody AmmoBatch input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ammoService.createBatch(input));
    }

    @PostMapping("/inbound")
    public ResponseEntity<AmmoBatch> inbound(@RequestBody AmmoInboundRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ammoService.inbound(req));
    }

    @PostMapping("/outbound")
    public AmmoBatch outbound(@RequestBody AmmoOutboundRequest req) {
        return ammoService.outbound(req);
    }

    @PostMapping("/scrap")
    public AmmoBatch scrap(@RequestBody AmmoScrapRequest req) {
        return ammoService.scrap(req);
    }

    @GetMapping("/batches/{id}/transactions")
    public List<AmmoTransaction> getTransactions(@PathVariable Long id) {
        return ammoService.getTransactionsByBatch(id);
    }

    @GetMapping("/batches/{id}/reconcile")
    public AmmoReconciliationResult reconcile(@PathVariable Long id) {
        return ammoService.reconcileBatch(id);
    }

    @PostMapping("/consumptions")
    public ResponseEntity<AmmoConsumption> recordConsumption(@RequestBody AmmoConsumptionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ammoService.recordConsumption(req));
    }

    @GetMapping("/issuances/{issuanceId}/consumptions")
    public List<AmmoConsumption> getConsumptions(@PathVariable Long issuanceId) {
        return ammoService.getConsumptionsByIssuance(issuanceId);
    }

    @PostMapping("/issuances/{issuanceId}/close")
    public void closeIssuance(@PathVariable Long issuanceId) {
        ammoService.closeIssuance(issuanceId);
    }

    @GetMapping("/issuances/{issuanceId}/validate")
    public void validateReconciliation(@PathVariable Long issuanceId) {
        ammoService.validateReconciliation(issuanceId);
    }

    @PostMapping("/inventory-checks")
    public ResponseEntity<AmmoInventoryCheck> createInventoryCheck(@RequestBody InventoryCheckRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ammoService.createInventoryCheck(req));
    }

    @PostMapping("/inventory-checks/{id}/adjust")
    public AmmoInventoryCheck adjustInventory(@PathVariable Long id, @RequestBody InventoryAdjustRequest req) {
        return ammoService.adjustInventory(id, req);
    }

    @GetMapping("/batches/{batchId}/inventory-checks")
    public List<AmmoInventoryCheck> getInventoryChecks(@PathVariable Long batchId) {
        return ammoService.getInventoryChecksByBatch(batchId);
    }

    @PostMapping("/shooting-records")
    public ResponseEntity<ShootingRecord> recordShooting(@RequestBody ShootingRecordRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ammoService.recordShooting(req));
    }

    @GetMapping("/issuances/{issuanceId}/shooting-records")
    public List<ShootingRecord> getShootingRecordsByIssuance(@PathVariable Long issuanceId) {
        return ammoService.getShootingRecordsByIssuance(issuanceId);
    }

    @GetMapping("/officers/{officerId}/shooting-records")
    public List<ShootingRecord> getShootingRecordsByOfficer(@PathVariable Long officerId) {
        return ammoService.getShootingRecordsByOfficer(officerId);
    }
}
