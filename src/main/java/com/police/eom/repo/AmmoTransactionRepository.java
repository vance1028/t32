package com.police.eom.repo;

import com.police.eom.domain.AmmoTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AmmoTransactionRepository extends JpaRepository<AmmoTransaction, Long> {

    List<AmmoTransaction> findByBatchIdOrderByOccurredAtDesc(Long batchId);

    List<AmmoTransaction> findByBatchIdAndOccurredAtBetweenOrderByOccurredAtAsc(Long batchId, LocalDateTime start, LocalDateTime end);

    List<AmmoTransaction> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);

    @Query("SELECT COALESCE(SUM(CASE WHEN t.transactionType = 'IN' THEN t.quantity " +
           "WHEN t.transactionType = 'RETURN' THEN t.quantity " +
           "WHEN t.transactionType = 'OUT' THEN -t.quantity " +
           "WHEN t.transactionType = 'SCRAP' THEN -t.quantity " +
           "WHEN t.transactionType = 'ADJUST_PLUS' THEN t.quantity " +
           "WHEN t.transactionType = 'ADJUST_MINUS' THEN -t.quantity ELSE 0 END), 0) " +
           "FROM AmmoTransaction t WHERE t.batchId = :batchId")
    int calculateBalanceByBatchId(@Param("batchId") Long batchId);
}
