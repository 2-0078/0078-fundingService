package com.pieceofcake.fundingservice.application;

import com.pieceofcake.fundingservice.dto.in.CancelParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.dto.in.CreateFundingRequestDto;
import com.pieceofcake.fundingservice.dto.in.ParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.dto.in.UpdateFundingRequestDto;
import com.pieceofcake.fundingservice.dto.out.GetFundingResponseDto;

import java.util.List;

public interface FundingService {
    //admin
    List<String> getFundingUuidList();
    GetFundingResponseDto getFunding(String fundingUuid);
    void createFunding(CreateFundingRequestDto createFundingRequestDto);
    void updateFunding(UpdateFundingRequestDto updateFundingRequestDto);
    void updateFundingStatus(UpdateFundingRequestDto updateFundingRequestDto);
    void deleteFunding(String fundingUuid);
    void participateFunding(ParticipateFundingRequestDto fundingJoinRequestDto);
    void cancelFunding(CancelParticipateFundingRequestDto cancelDto);
    int getRemainingPieces(String fundingUuid);
}
