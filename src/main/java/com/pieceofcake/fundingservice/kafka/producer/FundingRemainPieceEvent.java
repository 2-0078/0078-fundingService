package com.pieceofcake.fundingservice.kafka.producer;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundingRemainPieceEvent {
    private String fundingUuid;
    private Integer remainPieces;
}
