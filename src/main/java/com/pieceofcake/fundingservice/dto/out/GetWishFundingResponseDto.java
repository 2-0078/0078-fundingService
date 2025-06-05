package com.pieceofcake.fundingservice.dto.out;

import com.pieceofcake.fundingservice.entity.WishFunding;
import com.pieceofcake.fundingservice.vo.out.GetWishFundingResponseVo;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GetWishFundingResponseDto {
    private Long id;
    private String fundingUuid;
    private String memberUuid;
    private String productUuid;

    public static GetWishFundingResponseDto from(WishFunding entity) {
        return GetWishFundingResponseDto.builder()
                .id(entity.getId())
                .fundingUuid(entity.getFundingUuid())
                .memberUuid(entity.getMemberUuid())
                .productUuid(entity.getProductUuid())
                .build();
    }

    public GetWishFundingResponseVo toVo(){
        return GetWishFundingResponseVo.builder()
                .id(this.id)
                .fundingUuid(this.fundingUuid)
                .memberUuid(this.memberUuid)
                .productUuid(this.productUuid)
                .build();
    }
}
