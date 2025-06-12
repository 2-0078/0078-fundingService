package com.pieceofcake.fundingservice.funding.infrastructure.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePieceRequestDto {
    private String productUuid;
    private Integer totalPieces;

    @Builder
    public CreatePieceRequestDto(String productUuid, Integer totalPieces) {
        this.productUuid = productUuid;
        this.totalPieces = totalPieces;
    }
}
