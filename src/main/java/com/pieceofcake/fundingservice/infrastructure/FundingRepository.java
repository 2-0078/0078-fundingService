package com.pieceofcake.fundingservice.infrastructure;

import com.pieceofcake.fundingservice.entity.Funding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FundingRepository extends JpaRepository<Funding,Long> {
    Optional<Funding> findByFundingUuid(String fundingUuid);
}
