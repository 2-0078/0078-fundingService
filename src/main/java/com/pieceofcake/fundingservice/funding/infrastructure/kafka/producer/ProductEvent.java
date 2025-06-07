package com.pieceofcake.fundingservice.funding.infrastructure.kafka.producer;

import com.pieceofcake.fundingservice.funding.entity.FundingStatus;

import java.time.LocalDateTime;

public class ProductEvent {
    private String fundingUuid;
    private String productUuid;
    private Long fundingAmount; //Double 공모가
    private Long piecePrice; //Double 한 조각 가격
    private Integer totalPieces;
    private Integer remainingPieces;
    private LocalDateTime fundingDeadline;
    private FundingStatus fundingStatus;
}
