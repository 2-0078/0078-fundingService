package com.pieceofcake.fundingservice.vo.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundingResponseVo {
    private String fundingUuid;
    private String productUuid;
    private Long fundingAmount;
    private Long piecePrice;
    private Integer totalPieces;
    private Integer remainingPieces;
    private String fundingDeadline;
    private String fundingStatus;
}
