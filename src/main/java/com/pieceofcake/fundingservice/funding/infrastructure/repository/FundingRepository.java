package com.pieceofcake.fundingservice.funding.infrastructure.repository;

import com.pieceofcake.fundingservice.funding.entity.Funding;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FundingRepository extends JpaRepository<Funding,Long> {
    Optional<Funding> findByFundingUuid(String fundingUuid);

    @Query("SELECT f FROM Funding f WHERE f.fundingStatus = 'FUNDING'")
    List<Funding> findByFundingStatus();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM Funding f WHERE f.fundingUuid = :uuid")
    Optional<Funding> findByFundingUuidWithLock(@Param("uuid") String uuid);

//    @Modifying
//    @Query("UPDATE Funding f SET f.fundingStatus = '' f.fundingDeadline <= :now")
}
