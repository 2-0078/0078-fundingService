package com.pieceofcake.fundingservice.infrastructure;

import com.pieceofcake.fundingservice.entity.Funding;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface FundingRepository extends JpaRepository<Funding,Long> {
    Optional<Funding> findByFundingUuid(String fundingUuid);

     @Lock(LockModeType.PESSIMISTIC_WRITE)
     Optional<Funding> findByFundingUuidWithLock(String fundingUuid);
}
