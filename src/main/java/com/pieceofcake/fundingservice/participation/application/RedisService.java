package com.pieceofcake.fundingservice.participation.application;

import com.pieceofcake.fundingservice.funding.dto.in.CreateFundingRequestDto;
import com.pieceofcake.fundingservice.funding.dto.in.SetRedisFundingRequestDto;
import com.pieceofcake.fundingservice.funding.dto.in.UpdateFundingRequestDto;

public interface RedisService {
    int getRemainingPieces(String fundingUuid);
    double getPiecePrice(String fundingUuid);
    void setRemainingPieces(SetRedisFundingRequestDto setRedisFundingRequestDto);
    Long decreaseRemainPieces(String fundingUuid, int quantity);
    boolean increaseRemainPieces(String fundingUuid, int quantity);
}
