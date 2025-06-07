package com.pieceofcake.fundingservice.participation.application;

import com.pieceofcake.fundingservice.participation.dto.in.CancelParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.participation.dto.in.ParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.participation.dto.out.GetParticipateFundingResponseDto;

public interface FundingParticipationService {
    void participateFunding(ParticipateFundingRequestDto participateFundingRequestDto);
    void cancelParticipation(ParticipateFundingRequestDto cancelDto);
    GetParticipateFundingResponseDto getMyFundingParticipations(String fundingUuid, String memberUuid);
    boolean getMyFunding(ParticipateFundingRequestDto participateFundingRequestDto);
    int getMyTotalParticipationQuantity(ParticipateFundingRequestDto participateFundingRequestDto);
    int getRemainingPieces(String fundingUuid);
}
