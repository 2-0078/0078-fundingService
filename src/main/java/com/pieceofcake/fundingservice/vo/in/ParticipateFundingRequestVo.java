package com.pieceofcake.fundingservice.vo.in;

import lombok.Getter;

@Getter
public class ParticipateFundingRequestVo {
    private String fundingUuid;
    private String memberUuid;
    private Integer quantity;
}
