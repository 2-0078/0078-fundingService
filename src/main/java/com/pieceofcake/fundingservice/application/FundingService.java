package com.pieceofcake.fundingservice.application;

import com.pieceofcake.fundingservice.dto.in.FundingCreateRequestDto;
import com.pieceofcake.fundingservice.dto.in.FundingUpdateRequestDto;
import com.pieceofcake.fundingservice.entity.Funding;

public interface FundingService {
    //admin
    void createFunding(FundingCreateRequestDto fundingCreateRequestDto);
    void updateFunding(FundingUpdateRequestDto fundingUpdateRequestDto);
    void updateFundingStatus(FundingUpdateRequestDto fundingUpdateRequestDto);
    void deleteFunding(String fundingUuid);
}
