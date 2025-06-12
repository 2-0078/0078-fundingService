package com.pieceofcake.fundingservice.participation.infrastructure.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePaymentRequestDto {
    private Long amount;
    private Boolean isPositive;
    private MoneyHistoryType historyType;
    private String moneyHistoryDetail;

    @Builder
    public CreatePaymentRequestDto(
            Long amount,
            Boolean isPositive,
            MoneyHistoryType historyType,
            String moneyHistoryDetail
    ) {
        this.amount = amount;
        this.isPositive = isPositive;
        this.historyType = historyType;
        this.moneyHistoryDetail = moneyHistoryDetail;
    }
}
