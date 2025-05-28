package com.pieceofcake.fundingservice.dto.in;

import com.pieceofcake.fundingservice.entity.Funding;
import com.pieceofcake.fundingservice.entity.FundingStatus;
import com.pieceofcake.fundingservice.vo.in.FundingCreateRequestVo;
import com.pieceofcake.fundingservice.vo.in.FundingUpdateRequestVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundingUpdateRequestDto {
    private String fundingUuid;
//    private String productUuid;
    private Long fundingAmount;
    private Long piecePrice;
    private Integer totalPieces;
//    private Integer remainingPieces;
//    private LocalDateTime fundingDeadline;
    private FundingStatus fundingStatus;

    public static FundingUpdateRequestDto from(FundingUpdateRequestVo fundingUpdateRequestVo) {
        return FundingUpdateRequestDto.builder()
                .fundingUuid(fundingUpdateRequestVo.getFundingUuid())
//                .productUuid(fundingUpdateRequestVo.getProductUuid())
                .fundingAmount(fundingUpdateRequestVo.getFundingAmount())
                .piecePrice(fundingUpdateRequestVo.getPiecePrice())
                .totalPieces(fundingUpdateRequestVo.getTotalPieces())
//                .remainingPieces(fundingUpdateRequestVo.getTotalPieces())
                .fundingStatus(fundingUpdateRequestVo.getFundingStatus())
                .build();
    }

    public Funding toEntity(Funding funding){
        return Funding.builder()
                .id(funding.getId())
                .fundingUuid(fundingUuid)
                .productUuid(funding.getProductUuid())
                .fundingAmount(fundingAmount == null ? funding.getFundingAmount() : fundingAmount)
                .piecePrice(piecePrice == null ? funding.getPiecePrice() : piecePrice)
                .totalPieces(totalPieces == null ? funding.getTotalPieces() : totalPieces)
                .remainingPieces(totalPieces)
                .fundingDeadline(funding.getFundingDeadline())
                .fundingStatus(fundingStatus == null ? funding.getFundingStatus() : fundingStatus)
                .build();
    }
}
