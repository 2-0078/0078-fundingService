package com.pieceofcake.fundingservice.infrastructure;

import com.pieceofcake.fundingservice.entity.FundingParticipation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface FundingParticipationRepository extends JpaRepository<FundingParticipation, Long> {
//    @Lock(LockModeType.PESSIMISTIC_WRITE)


}
