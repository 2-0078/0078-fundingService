package com.pieceofcake.fundingservice.funding.infrastructure.repository;

import com.pieceofcake.fundingservice.funding.entity.WishFunding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishFundingRepository extends JpaRepository<WishFunding,Long> {
    List<WishFunding> getByMemberUuid(String memberUuid);
    Boolean existsByMemberUuidAndFundingUuid(String memberUuid, String fundingUuid);
}
