package com.pieceofcake.fundingservice.infrastructure;

import com.pieceofcake.fundingservice.dto.out.GetParticipateFundingResponseDto;
import com.pieceofcake.fundingservice.entity.FundingParticipation;
import com.pieceofcake.fundingservice.entity.ParticipateStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FundingParticipationRepository extends JpaRepository<FundingParticipation, Long> {
    //공모 상품의 참여 내역 전체 조회
    List<FundingParticipation> findByFundingUuidAndMemberUuidAndParticipateStatus(String fundingUuid, String memberUuid, ParticipateStatus participateStatus);

    @Modifying
    @Query("UPDATE FundingParticipation f " +
            "SET f.participateStatus = :participate_status " +
            "WHERE f.fundingUuid = :funding_uuid AND f.memberUuid = :member_uuid")
    void cancelParticipation(@Param("funding_uuid") String fundingUuid,
                             @Param("member_uuid") String memberUuid,
                             @Param("participate_status") ParticipateStatus participateStatus);
}
