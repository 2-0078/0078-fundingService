package com.pieceofcake.fundingservice.funding.infrastructure.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePieceRequestDto {
    private String productUuid;
    private int totalQuantity;

    @Builder
    public CreatePieceRequestDto(String productUuid, int totalQuantity) {
        this.productUuid = productUuid;
        this.totalQuantity = totalQuantity;
    }
}
