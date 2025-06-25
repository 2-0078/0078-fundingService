package com.pieceofcake.fundingservice.kafka.producer;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletedFundingEvent {
    private String fundingUuid;
    private Long piecePrice;
}
