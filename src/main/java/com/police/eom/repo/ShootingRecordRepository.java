package com.police.eom.repo;

import com.police.eom.domain.ShootingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShootingRecordRepository extends JpaRepository<ShootingRecord, Long> {

    List<ShootingRecord> findByIssuanceId(Long issuanceId);

    List<ShootingRecord> findByOfficerId(Long officerId);

    List<ShootingRecord> findByFirearmId(Long firearmId);

    List<ShootingRecord> findByBatchId(Long batchId);

    List<ShootingRecord> findByOfficerIdAndOccurredAtBetween(Long officerId, LocalDateTime start, LocalDateTime end);
}
