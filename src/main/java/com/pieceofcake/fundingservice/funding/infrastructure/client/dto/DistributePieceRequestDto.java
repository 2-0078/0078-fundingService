package com.pieceofcake.fundingservice.funding.infrastructure.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DistributePieceRequestDto {
    private String productUuid;
    private Integer pieceQuantity;
    @Builder
    public DistributePieceRequestDto(String productUuid, Integer pieceQuantity) {
        this.productUuid = productUuid;
        this.pieceQuantity = pieceQuantity;
    }
}
