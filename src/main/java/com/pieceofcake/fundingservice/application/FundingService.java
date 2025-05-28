package com.pieceofcake.fundingservice.application;

import com.pieceofcake.fundingservice.dto.in.FundingCreateRequestDto;
import com.pieceofcake.fundingservice.dto.in.FundingUpdateRequestDto;
import com.pieceofcake.fundingservice.dto.out.FundingResponseDto;
import com.pieceofcake.fundingservice.entity.Funding;

import java.util.List;

public interface FundingService {
    //admin
    List<FundingResponseDto> getFundingList();
    List<String> getFundingUuidList();
    FundingResponseDto getFunding(String fundingUuid);
    void createFunding(FundingCreateRequestDto fundingCreateRequestDto);
    void updateFunding(FundingUpdateRequestDto fundingUpdateRequestDto);
    void updateFundingStatus(FundingUpdateRequestDto fundingUpdateRequestDto);
    void deleteFunding(String fundingUuid);
}
