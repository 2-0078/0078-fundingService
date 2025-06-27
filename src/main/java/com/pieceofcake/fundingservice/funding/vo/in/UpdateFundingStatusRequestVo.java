package com.pieceofcake.fundingservice.funding.vo.in;

import com.pieceofcake.fundingservice.funding.entity.FundingStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateFundingStatusRequestVo {
    private String fundingUuid;
    private FundingStatus fundingStatus;

    @Builder
    public UpdateFundingStatusRequestVo(String fundingUuid, FundingStatus fundingStatus) {
        this.fundingUuid = fundingUuid;
        this.fundingStatus = fundingStatus;
    }
}
