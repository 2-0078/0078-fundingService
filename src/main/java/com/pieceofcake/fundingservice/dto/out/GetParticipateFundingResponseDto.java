package com.pieceofcake.fundingservice.dto.out;

import com.pieceofcake.fundingservice.entity.FundingParticipation;
import com.pieceofcake.fundingservice.entity.ParticipateStatus;
import com.pieceofcake.fundingservice.vo.out.GetParticipateFundingResponseVo;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GetParticipateFundingResponseDto {
    private String participationUuid;
    private String fundingUuid;
    private String memberUuid;
    private Integer quantity;
    private ParticipateStatus fundingStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GetParticipateFundingResponseDto from(FundingParticipation fundingParticipation) {
        return GetParticipateFundingResponseDto.builder()
                .participationUuid(fundingParticipation.getParticipationUuid())
                .fundingUuid(fundingParticipation.getFundingUuid())
                .memberUuid(fundingParticipation.getMemberUuid())
                .quantity(fundingParticipation.getQuantity())
                .fundingStatus(fundingParticipation.getParticipateStatus())
                .createdAt(fundingParticipation.getCreatedAt())
                .updatedAt(fundingParticipation.getUpdatedAt())
                .build();
    }

    public GetParticipateFundingResponseVo toVo(){
        return GetParticipateFundingResponseVo.builder()
                .participationUuid(participationUuid)
                .fundingUuid(fundingUuid)
                .memberUuid(memberUuid)
                .quantity(quantity)
                .fundingStatus(fundingStatus)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
