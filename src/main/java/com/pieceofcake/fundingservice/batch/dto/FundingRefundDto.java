package com.pieceofcake.fundingservice.batch.dto;

import com.pieceofcake.fundingservice.participation.dto.in.ParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.participation.entity.ParticipateStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class FundingRefundDto {
    private String fundingUuid;
    private String memberUuid;
    private Long totalRefund;
    private Integer totalQuantity;
    private LocalDate cancelDate;
    private LocalDateTime refundDate;

    @Builder
    public FundingRefundDto(
            String fundingUuid, String memberUuid, Long totalRefund, Integer totalQuantity,
            LocalDate cancelDate, LocalDateTime refundDate) {
        this.fundingUuid = fundingUuid;
        this.memberUuid = memberUuid;
        this.totalRefund = totalRefund;
        this.totalQuantity = totalQuantity;
        this.cancelDate = cancelDate;
        this.refundDate = refundDate;
    }

    public ParticipateFundingRequestDto toParticipateFundingRequestDto(){
        return ParticipateFundingRequestDto.builder()
                .fundingUuid(fundingUuid)
                .memberUuid(memberUuid)
                .quantity(totalQuantity)
                .participateStatus(ParticipateStatus.REFUNDED)
                .build();
    }
}
