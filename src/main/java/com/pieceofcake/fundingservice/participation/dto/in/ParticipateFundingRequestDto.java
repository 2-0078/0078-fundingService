package com.pieceofcake.fundingservice.participation.dto.in;

import com.pieceofcake.fundingservice.participation.entity.ParticipateStatus;
import com.pieceofcake.fundingservice.participation.vo.in.ParticipateFundingRequestVo;
import com.pieceofcake.fundingservice.participation.entity.FundingParticipation;
import lombok.*;

import java.util.UUID;

@Getter
@ToString
@NoArgsConstructor
public class ParticipateFundingRequestDto {
    private String fundingUuid;
    private String memberUuid;
    private Integer quantity;
    private ParticipateStatus participateStatus;

    @Builder
    public ParticipateFundingRequestDto(
            String fundingUuid,
            String memberUuid,
            Integer quantity,
            ParticipateStatus participateStatus) {
        this.fundingUuid = fundingUuid;
        this.memberUuid = memberUuid;
        this.quantity = quantity;
        this.participateStatus = participateStatus;
    }

    public static ParticipateFundingRequestDto from(ParticipateFundingRequestVo vo, String memberUuid) {
        return ParticipateFundingRequestDto.builder()
                .fundingUuid(vo.getFundingUuid())
                .memberUuid(memberUuid)
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

    public FundingParticipation toEntity(int quantity){
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
