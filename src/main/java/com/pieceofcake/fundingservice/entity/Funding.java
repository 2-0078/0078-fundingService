package com.pieceofcake.fundingservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Funding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fundingUuid;
    private String productUuid;
    private String findingAmount;
    private String piecePrice;
    private String totalPiece;
    private String remainingPiece;
    private String fundingDeadline;
    private FundingStatus fundingStatus;
}
