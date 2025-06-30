package com.pieceofcake.fundingservice.funding.infrastructure.repository;

import com.pieceofcake.fundingservice.funding.entity.WishFunding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishFundingRepository extends JpaRepository<WishFunding,Long> {
    List<WishFunding> getByMemberUuid(String memberUuid);
    Optional<WishFunding> getByMemberUuidAndFundingUuid(String memberUuid, String fundingUuid);
    Boolean existsByMemberUuidAndFundingUuid(String memberUuid, String fundingUuid);
}
