package com.pieceofcake.fundingservice.dto.in;

import com.pieceofcake.fundingservice.entity.Funding;
import com.pieceofcake.fundingservice.entity.FundingStatus;
import com.pieceofcake.fundingservice.vo.in.FundingCreateRequestVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundingCreateRequestDto {
    private String fundingUuid;
    private String productUuid;
    private Long fundingAmount;
    private Long piecePrice;
    private Integer totalPieces;
    private Integer remainingPieces;
    private LocalDateTime fundingDeadline;
    private FundingStatus fundingStatus;

    public static FundingCreateRequestDto from(FundingCreateRequestVo fundingCreateRequestVo) {
        return FundingCreateRequestDto.builder()
                .fundingUuid(createFundingUuid())
                .productUuid(fundingCreateRequestVo.getProductUuid())
                .fundingAmount(fundingCreateRequestVo.getFundingAmount())
                .piecePrice(fundingCreateRequestVo.getPiecePrice())
                .totalPieces(fundingCreateRequestVo.getTotalPieces())
                .remainingPieces(fundingCreateRequestVo.getTotalPieces())
                .fundingDeadline(createFundingDeadline())
                .fundingStatus(fundingCreateRequestVo.getFundingStatus())
                .build();
    }

    public Funding toEntity(){
        return Funding.builder()
                .fundingUuid(fundingUuid)
                .productUuid(productUuid)
                .fundingAmount(fundingAmount)
                .piecePrice(piecePrice)
                .totalPieces(totalPieces)
                .remainingPieces(remainingPieces)
                .fundingDeadline(fundingDeadline)
                .fundingStatus(fundingStatus)
                .build();
    }

    private static String createFundingUuid(){
        return "F"+ UUID.randomUUID().toString().substring(0,5);
    }

    private static LocalDateTime createFundingDeadline(){
        return LocalDateTime.now().plusMonths(1);
    }
}
