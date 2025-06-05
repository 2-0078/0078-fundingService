package com.pieceofcake.fundingservice.vo.out;

import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GetWishFundingResponseVo {
    private Long id;
    private String fundingUuid;
    private String productUuid;
    private String memberUuid;
}
