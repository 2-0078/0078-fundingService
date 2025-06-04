package com.pieceofcake.fundingservice.application;

import com.pieceofcake.fundingservice.dto.in.ParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.dto.out.GetParticipateFundingResponseDto;

import java.util.List;

public interface FundingParticipationService {
    void joinFunding(ParticipateFundingRequestDto participateFundingRequestDto);
    void leaveFunding();
    GetParticipateFundingResponseDto getMyFundingParticipations(String fundingUuid, String memberUuid);
    void getMyFundings(String memberUuid);
    int getMyTotalParticipationQuantity(String fundingUuid, String memberUuid);
    void cancelParticipation(String fundingUuid, String memberUuid);
}
