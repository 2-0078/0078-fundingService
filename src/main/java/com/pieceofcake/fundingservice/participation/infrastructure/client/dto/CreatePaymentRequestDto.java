package com.pieceofcake.fundingservice.participation.infrastructure.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePaymentRequestDto {
    private String fundingUuid;
    private String memberUuid;
    private Double totalPrice;
    private String status;

    @Builder
    public CreatePaymentRequestDto(String fundingUuid, String memberUuid, Double totalPrice, String status) {
        this.fundingUuid = fundingUuid;
        this.memberUuid = memberUuid;
        this.totalPrice = totalPrice;
        this.status = status;
    }
}
