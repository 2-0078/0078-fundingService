package com.pieceofcake.fundingservice.kafka.producer;

import com.pieceofcake.fundingservice.funding.entity.FundingStatus;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundingEvent {
    private String fundingUuid;
    private String productUuid;
    private Long fundingAmount;
    private Long piecePrice;
    private Integer totalPieces;
    private Integer remainingPieces;
    private String fundingDeadline;
    private String fundingStatus;
}
