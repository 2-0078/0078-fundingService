package com.pieceofcake.fundingservice.dto.in;

import com.pieceofcake.fundingservice.entity.FundingParticipation;
import com.pieceofcake.fundingservice.entity.ParticipateStatus;
import com.pieceofcake.fundingservice.vo.in.ParticipateFundingRequestVo;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParticipateFundingRequestDto {
    private String fundingUuid;
    private String memberUuid;
    private Integer quantity;
    private ParticipateStatus participateStatus;

    public static ParticipateFundingRequestDto from(ParticipateFundingRequestVo vo) {
        return ParticipateFundingRequestDto.builder()
                .fundingUuid(vo.getFundingUuid())
                .memberUuid(vo.getMemberUuid())
                .quantity(vo.getQuantity())
                .participateStatus(ParticipateStatus.JOIN)
                .build();
    }

    public FundingParticipation toEntity(){
        return FundingParticipation.builder()
                .participationUuid(createParticipationUuid())
                .fundingUuid(fundingUuid)
                .memberUuid(memberUuid)
                .quantity(quantity)
                .participateStatus(participateStatus)
                .build();
    }

    private String createParticipationUuid(){
        return UUID.randomUUID().toString().substring(0,32);
    }
}
