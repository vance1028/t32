package com.police.eom.repo;

import com.police.eom.domain.AmmoConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmmoConsumptionRepository extends JpaRepository<AmmoConsumption, Long> {

    List<AmmoConsumption> findByIssuanceId(Long issuanceId);

    List<AmmoConsumption> findByBatchId(Long batchId);

    @Query("SELECT COALESCE(SUM(c.consumedQuantity), 0) FROM AmmoConsumption c WHERE c.issuanceId = :issuanceId")
    int sumConsumedByIssuanceId(@Param("issuanceId") Long issuanceId);
}
