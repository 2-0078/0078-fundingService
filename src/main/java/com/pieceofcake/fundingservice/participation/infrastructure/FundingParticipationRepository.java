package com.pieceofcake.fundingservice.participation.infrastructure;

import com.pieceofcake.fundingservice.participation.entity.ParticipateStatus;
import com.pieceofcake.fundingservice.participation.entity.FundingParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FundingParticipationRepository extends JpaRepository<FundingParticipation, Long> {
    //공모 상품의 참여 내역 전체 조회
    List<FundingParticipation> findByFundingUuidAndMemberUuidAndParticipateStatus(String fundingUuid, String memberUuid, ParticipateStatus participateStatus);

}
