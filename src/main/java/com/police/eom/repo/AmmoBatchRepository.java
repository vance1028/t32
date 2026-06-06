package com.police.eom.repo;

import com.police.eom.domain.AmmoBatch;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmmoBatchRepository extends JpaRepository<AmmoBatch, Long> {

    boolean existsByBatchNo(String batchNo);

    List<AmmoBatch> findByStatus(String status);

    List<AmmoBatch> findByCaliber(String caliber);

    Optional<AmmoBatch> findByBatchNo(String batchNo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM AmmoBatch b WHERE b.id = :id")
    Optional<AmmoBatch> findByIdWithLock(@Param("id") Long id);
}
