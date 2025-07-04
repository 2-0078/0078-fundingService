package com.pieceofcake.fundingservice.kafka.producer;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AlertEvent {
    private String Key;
    private String message;
    private String memberUuid;
    private Boolean commonAlert;

    @Builder
    public AlertEvent(String Key, String message, String memberUuid, Boolean commonAlert) {
        this.Key = Key;
        this.message = message;
        this.memberUuid = memberUuid;
        this.commonAlert = commonAlert;
    }
}
