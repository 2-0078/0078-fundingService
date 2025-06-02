package com.pieceofcake.fundingservice.dto.in;

import com.pieceofcake.fundingservice.entity.FundingParticipation;
import com.pieceofcake.fundingservice.entity.MemberStatus;
import com.pieceofcake.fundingservice.vo.in.ParticipateFundingRequestVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipateFundingRequestDto {
    private String fundingUuid;
    private String memberUuid;
    private Integer quantity;
    private MemberStatus fundingStatus;

    public static ParticipateFundingRequestDto from(ParticipateFundingRequestVo vo) {
        return ParticipateFundingRequestDto.builder()
                .fundingUuid(vo.getFundingUuid())
                .memberUuid(vo.getMemberUuid())
                .quantity(vo.getQuantity())
                .fundingStatus(MemberStatus.JOIN)
                .build();
    }

    public FundingParticipation toEntity(){
        return FundingParticipation.builder()
                .participationUuid(createParticipationUuid())
                .fundingUuid(fundingUuid)
                .memberUuid(memberUuid)
                .fundingStatus(fundingStatus)
                .build();
    }

    private String createParticipationUuid(){
        return "P"+ UUID.randomUUID().toString().substring(0,8);
    }
}
