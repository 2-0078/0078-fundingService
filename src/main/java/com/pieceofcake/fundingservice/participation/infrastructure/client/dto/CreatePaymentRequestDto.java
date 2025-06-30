package com.pieceofcake.fundingservice.participation.infrastructure.client.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreatePaymentRequestDto {
    private String memberUuid;
    private Long amount;
    private Boolean isPositive;
    private MoneyHistoryType historyType;
    private String moneyHistoryDetail;

    @Builder
    public CreatePaymentRequestDto(
            String memberUuid,
            Long amount,
            Boolean isPositive,
            MoneyHistoryType historyType,
            String moneyHistoryDetail
    ) {
        this.memberUuid = memberUuid;
        this.amount = amount;
        this.isPositive = isPositive;
        this.historyType = historyType;
        this.moneyHistoryDetail = moneyHistoryDetail;
    }
}
