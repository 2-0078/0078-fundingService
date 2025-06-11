package com.pieceofcake.fundingservice.funding.dto.in;

import com.pieceofcake.fundingservice.funding.entity.Funding;
import com.pieceofcake.fundingservice.funding.entity.FundingStatus;
import com.pieceofcake.fundingservice.funding.vo.in.UpdateFundingRequestVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFundingRequestDto {
    private String fundingUuid;
//    private String productUuid;
    private Long fundingAmount;
    private Long piecePrice;
    private Integer totalPieces;
    private Integer remainingPieces;
//    private LocalDateTime fundingDeadline;
    private FundingStatus fundingStatus;

    public static UpdateFundingRequestDto from(UpdateFundingRequestVo updateFundingRequestVo) {
        return UpdateFundingRequestDto.builder()
                .fundingUuid(updateFundingRequestVo.getFundingUuid())
//                .productUuid(fundingUpdateRequestVo.getProductUuid())
                .fundingAmount(updateFundingRequestVo.getFundingAmount())
                .piecePrice(updateFundingRequestVo.getPiecePrice())
                .totalPieces(updateFundingRequestVo.getTotalPieces())
                .remainingPieces(updateFundingRequestVo.getTotalPieces())
                .fundingStatus(updateFundingRequestVo.getFundingStatus())
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
