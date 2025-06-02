package com.pieceofcake.fundingservice.vo.in;

import lombok.Getter;

@Getter
public class CancelFundingRequestVo {
    private String fundingUuid;
    private String memberUuid;
}
