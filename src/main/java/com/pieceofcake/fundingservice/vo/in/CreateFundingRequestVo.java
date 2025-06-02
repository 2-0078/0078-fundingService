package com.pieceofcake.fundingservice.vo.in;

import com.pieceofcake.fundingservice.entity.FundingStatus;
import lombok.Getter;

@Getter
public class CreateFundingRequestVo {
    private String productUuid;
    private Long fundingAmount;
    private Long piecePrice;
    private Integer totalPieces;
//    private LocalDateTime fundingDeadline;
    private FundingStatus fundingStatus;
}
