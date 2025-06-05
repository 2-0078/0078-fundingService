package com.pieceofcake.fundingservice.infrastructure;

import com.pieceofcake.fundingservice.entity.WishFunding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface WishFundingRepository extends JpaRepository<WishFunding,Long> {
    List<WishFunding> getByMemberUuid(String memberUuid);
    Boolean existsByMemberUuidAndFundingUuid(String memberUuid, String fundingUuid);
}
