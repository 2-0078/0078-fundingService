package com.pieceofcake.fundingservice.funding.vo.in;

import lombok.Getter;

@Getter
public class CreateWishFundingRequestVo {
    private String memberUuid;
    private String productUuid;
    private String fundingUuid;
}
