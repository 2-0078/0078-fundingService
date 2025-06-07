package com.pieceofcake.fundingservice.funding.vo.in;

import com.pieceofcake.fundingservice.funding.entity.FundingStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UpdateFundingRequestVo {
    private String fundingUuid;
    private String productUuid;
    private Double fundingAmount;
    private Double piecePrice;
    private Integer totalPieces;
    private LocalDateTime fundingDeadline;
    private FundingStatus fundingStatus;
}
