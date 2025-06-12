package com.pieceofcake.fundingservice.participation.infrastructure.client.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MoneyHistoryType {
    FUNDING("공모"),
    REFUND("환불");

    private final String label;
}
