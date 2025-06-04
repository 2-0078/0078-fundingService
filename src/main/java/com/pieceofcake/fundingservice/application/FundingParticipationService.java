package com.pieceofcake.fundingservice.application;

import com.pieceofcake.fundingservice.dto.in.ParticipateFundingRequestDto;

public interface FundingParticipationService {
    void joinFunding(ParticipateFundingRequestDto participateFundingRequestDto);
    void leaveFunding();
    void getMyFunding();
}
