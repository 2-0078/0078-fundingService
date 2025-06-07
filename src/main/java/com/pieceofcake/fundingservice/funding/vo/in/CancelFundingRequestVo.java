package com.pieceofcake.fundingservice.funding.vo.in;

import lombok.Getter;

@Getter
public class CancelFundingRequestVo {
    private String fundingUuid;
    private String memberUuid;
}
