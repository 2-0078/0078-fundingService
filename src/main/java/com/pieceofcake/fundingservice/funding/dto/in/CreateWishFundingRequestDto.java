package com.pieceofcake.fundingservice.funding.dto.in;

import com.pieceofcake.fundingservice.funding.entity.WishFunding;
import com.pieceofcake.fundingservice.funding.vo.in.CreateWishFundingRequestVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWishFundingRequestDto {
    private String memberUuid;
    private String fundingUuid;
    private String productUuid;

    public static CreateWishFundingRequestDto from(CreateWishFundingRequestVo createWishFundingRequestVo, String memberUuid) {
        return CreateWishFundingRequestDto.builder()
                .memberUuid(memberUuid)
                .fundingUuid(createWishFundingRequestVo.getFundingUuid())
                .productUuid(createWishFundingRequestVo.getProductUuid())
                .build();
    }

    public WishFunding toEntity(){
        return WishFunding.builder()
                .fundingUuid(fundingUuid)
                .memberUuid(memberUuid)
                .productUuid(productUuid)
                .build();
    }
}
