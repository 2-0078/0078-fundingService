package com.pieceofcake.fundingservice.kafka.producer;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletedFundingEvent {
    private String fundingUuid;
    private String productUuid;
    private Long piecePrice;
    private Integer totalPieces;
    private Boolean isTrading;
}
