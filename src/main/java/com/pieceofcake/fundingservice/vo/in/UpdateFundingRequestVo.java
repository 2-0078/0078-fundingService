package com.pieceofcake.fundingservice.vo.in;

import com.pieceofcake.fundingservice.entity.FundingStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UpdateFundingRequestVo {
    private String fundingUuid;
    private String productUuid;
    private Long fundingAmount;
    private Long piecePrice;
    private Integer totalPieces;
    private LocalDateTime fundingDeadline;
    private FundingStatus fundingStatus;
}
