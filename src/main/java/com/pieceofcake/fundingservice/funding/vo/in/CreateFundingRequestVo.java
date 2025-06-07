package com.pieceofcake.fundingservice.funding.vo.in;

import com.pieceofcake.fundingservice.funding.entity.FundingStatus;
import lombok.Getter;

@Getter
public class CreateFundingRequestVo {
    private String productUuid;
    private Double fundingAmount;
    private Double piecePrice;
    private Integer totalPieces;
//    private LocalDateTime fundingDeadline;
    private FundingStatus fundingStatus;
}
