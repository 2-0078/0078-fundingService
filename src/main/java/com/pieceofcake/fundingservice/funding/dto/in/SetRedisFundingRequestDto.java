package com.pieceofcake.fundingservice.funding.dto.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SetRedisFundingRequestDto {
    private String fundingUuid;
    private Double piecePrice;
    private Integer totalPieces;
    private Integer remainingPieces;

    @Builder
    public SetRedisFundingRequestDto(String fundingUuid, Double piecePrice, Integer totalPieces, Integer remainingPieces) {
        this.fundingUuid = fundingUuid;
        this.piecePrice = piecePrice;
        this.totalPieces = totalPieces;
        this.remainingPieces = remainingPieces;
    }
}
