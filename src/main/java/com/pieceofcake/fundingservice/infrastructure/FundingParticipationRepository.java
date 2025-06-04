package com.pieceofcake.fundingservice.infrastructure;

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
    List<FundingParticipation> findByFundingUuidAndMemberUuid(String fundingUuid, String memberUuid);

    @Query("SELECT SUM(f.quantity) " +
            "FROM FundingParticipation f " +
            "WHERE  f.fundingUuid = :funding_uuid AND f.memberUuid = :member_uuid AND f.participateStatus = 'JOIN' ")
    Optional<Integer> findMyTotalParticipationQuantity(
            @Param("funding_uuid") String fundingUuid,
            @Param("member_uuid") String memberUuid);

    @Modifying
    @Query("UPDATE FundingParticipation f " +
            "SET f.participateStatus = :participate_status " +
            "WHERE f.fundingUuid = :funding_uuid AND f.memberUuid = :member_uuid")
    void cancelParticipation(@Param("funding_uuid") String fundingUuid,
                             @Param("member_uuid") String memberUuid,
                             @Param("participate_status") ParticipateStatus participateStatus);
}
