package com.pieceofcake.fundingservice.funding.infrastructure.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DistributePieceRequestDto {
    private String productUuid;
    private Integer pieceQuantity;
    private Boolean applyStatus;

    @Builder
    public DistributePieceRequestDto(String productUuid, Integer pieceQuantity, Boolean applyStatus) {
        this.productUuid = productUuid;
        this.pieceQuantity = pieceQuantity;
        this.applyStatus = applyStatus;
    }
}
