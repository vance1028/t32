package com.police.eom.repo;

import com.police.eom.domain.AmmoInventoryCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AmmoInventoryCheckRepository extends JpaRepository<AmmoInventoryCheck, Long> {

    List<AmmoInventoryCheck> findByBatchIdOrderByCheckDateDesc(Long batchId);

    List<AmmoInventoryCheck> findByCheckStatus(String checkStatus);

    List<AmmoInventoryCheck> findByCheckDateBetween(LocalDate startDate, LocalDate endDate);
}
