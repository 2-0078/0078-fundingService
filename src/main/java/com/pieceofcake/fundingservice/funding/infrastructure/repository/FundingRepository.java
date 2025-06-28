package com.pieceofcake.fundingservice.funding.infrastructure.repository;

import com.pieceofcake.fundingservice.funding.entity.Funding;
import com.pieceofcake.fundingservice.funding.entity.FundingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FundingRepository extends JpaRepository<Funding,Long> {
    Optional<Funding> findByFundingUuid(String fundingUuid);

    List<Funding> findByFundingStatus(FundingStatus fundingStatus);
    
    // 페이징을 위한 메서드들
    Page<Funding> findByFundingStatus(FundingStatus fundingStatus, Pageable pageable);
    Page<Funding> findAll(Pageable pageable);

    @Query("SELECT f FROM Funding f WHERE f.fundingStatus = 'FUNDING'")
    List<Funding> findByFundingStatus();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM Funding f WHERE f.fundingUuid = :uuid")
    Optional<Funding> findByFundingUuidWithLock(@Param("uuid") String uuid);

    @Query("select f.productUuid from Funding f where f.fundingUuid = :fundingUuid")
    Optional<String> findProductUuidByFundingUuid(@Param("fundingUuid") String fundingUuid);

//    @Modifying
//    @Query("UPDATE Funding f SET f.fundingStatus = '' f.fundingDeadline <= :now")
}
