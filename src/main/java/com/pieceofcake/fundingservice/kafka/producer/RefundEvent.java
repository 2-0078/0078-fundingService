package com.pieceofcake.fundingservice.kafka.producer;

import lombok.*;

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
