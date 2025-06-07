package com.pieceofcake.fundingservice.funding.dto.out;

import com.pieceofcake.fundingservice.funding.entity.Funding;
import com.pieceofcake.fundingservice.funding.entity.FundingStatus;
import com.pieceofcake.fundingservice.funding.vo.out.GetFundingResponseVo;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GetFundingResponseDto {
    private String fundingUuid;
    private String productUuid;
    private Double fundingAmount;
    private Double piecePrice;
    private Integer totalPieces;
    private Integer remainingPieces;
    private String fundingDeadline;
    private FundingStatus fundingStatus;

    public static GetFundingResponseDto from(Funding funding){
        return GetFundingResponseDto.builder()
                .fundingUuid(funding.getFundingUuid())
                .productUuid(funding.getProductUuid())
                .fundingAmount(funding.getFundingAmount())
                .piecePrice(funding.getPiecePrice())
                .totalPieces(funding.getTotalPieces())
                .remainingPieces(funding.getRemainingPieces())
                .fundingDeadline(funding.getFundingDeadline().toString())
                .fundingStatus(funding.getFundingStatus())
                .build();
    }

    public GetFundingResponseVo toVo(){
        return GetFundingResponseVo.builder()
                .fundingUuid(fundingUuid)
                .productUuid(productUuid)
                .fundingAmount(fundingAmount)
                .piecePrice(piecePrice)
                .totalPieces(totalPieces)
                .remainingPieces(remainingPieces)
                .fundingDeadline(fundingDeadline)
                .fundingStatus(fundingStatus.toString())
                .build();
    }
}
