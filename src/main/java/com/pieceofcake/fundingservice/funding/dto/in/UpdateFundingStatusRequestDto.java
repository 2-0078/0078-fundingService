package com.pieceofcake.fundingservice.funding.dto.in;

import com.pieceofcake.fundingservice.funding.entity.FundingStatus;
import com.pieceofcake.fundingservice.funding.vo.in.UpdateFundingStatusRequestVo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateFundingStatusRequestDto {
    private String fundingUuid;
    private FundingStatus fundingStatus;

    @Builder
    public UpdateFundingStatusRequestDto(String fundingUuid, FundingStatus fundingStatus) {
        this.fundingUuid = fundingUuid;
        this.fundingStatus = fundingStatus;
    }

    public static UpdateFundingStatusRequestDto from(UpdateFundingStatusRequestVo updateFundingStatusRequestVo) {
        return UpdateFundingStatusRequestDto.builder()
                .fundingUuid(updateFundingStatusRequestVo.getFundingUuid())
                .fundingStatus(updateFundingStatusRequestVo.getFundingStatus())
                .build();
    }


}
