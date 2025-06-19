package com.pieceofcake.fundingservice.funding.infrastructure.kafka.producer;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundEvent {
    private String memberUuid;
    private String fundingUuid;
    private Long totalRefund;
    private String cancelDate;
}
