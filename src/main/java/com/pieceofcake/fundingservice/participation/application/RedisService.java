package com.pieceofcake.fundingservice.participation.application;

import com.pieceofcake.fundingservice.funding.dto.in.CreateFundingRequestDto;
import com.pieceofcake.fundingservice.funding.dto.in.SetRedisFundingRequestDto;
import com.pieceofcake.fundingservice.funding.dto.in.UpdateFundingRequestDto;

public interface RedisService {
    int getRemainingPieces(String fundingUuid);
    long getPiecePrice(String fundingUuid);
    void setRemainingPieces(SetRedisFundingRequestDto setRedisFundingRequestDto);
    void deleteRemainingPieces(String fundingUuid);
    long decreaseRemainPieces(String fundingUuid, int quantity);
    boolean increaseRemainPieces(String fundingUuid, int quantity);
}
