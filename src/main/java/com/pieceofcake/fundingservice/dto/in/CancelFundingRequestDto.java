package com.pieceofcake.fundingservice.dto.in;

import com.pieceofcake.fundingservice.vo.in.CancelFundingRequestVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelFundingRequestDto {
    private String fundingUuid;
    private String memberUuid;

    public static CancelFundingRequestDto from(CancelFundingRequestVo cancelFundingRequestVo) {
        return CancelFundingRequestDto.builder()
                .fundingUuid(cancelFundingRequestVo.getFundingUuid())
                .memberUuid(cancelFundingRequestVo.getMemberUuid())
                .build();
    }
}
