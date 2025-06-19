package com.pieceofcake.fundingservice.funding.entity;

import com.pieceofcake.fundingservice.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Funding extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "funding_uuid", unique = true, nullable = false)
    private String fundingUuid;

    @Column(name = "product_uuid", nullable = false)
    private String productUuid;

    @Column(name = "funding_amount", nullable = false)
    private Long fundingAmount;

    @Column(name = "piece_price", nullable = false)
    private Long piecePrice;

    @Column(name = "total_pieces", nullable = false)
    private Integer totalPieces;

    @Column(name = "remaining_pieces", nullable = false)
    private Integer remainingPieces;

    @Column(name = "funding_deadline", nullable = false)
    private LocalDateTime fundingDeadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "funding_status", nullable = false)
    private FundingStatus fundingStatus;

    @Column(name = "is_deleted")
    private Boolean isDeleted;


    @Builder
    public Funding(
            Long id,
            String fundingUuid,
            String productUuid,
            Long fundingAmount,
            Long piecePrice,
            Integer totalPieces,
            Integer remainingPieces,
            LocalDateTime fundingDeadline,
            FundingStatus fundingStatus,
            Boolean isDeleted
    ) {
        this.id = id;
        this.fundingUuid = fundingUuid;
        this.productUuid = productUuid;
        this.fundingAmount = fundingAmount;
        this.piecePrice = piecePrice;
        this.totalPieces = totalPieces;
        this.isDeleted = isDeleted;
        this.remainingPieces = remainingPieces;
        this.fundingStatus = fundingStatus;
        this.remainingPieces = totalPieces;
        this.fundingDeadline = fundingDeadline;
    }

    public void updateFundingStatus(FundingStatus fundingStatus){
        this.fundingStatus = fundingStatus;
    }

    public void updateRemainingPieces(Integer remainingPieces){
        this.remainingPieces = remainingPieces;
    }

    public void deleteFunding(){
        this.isDeleted = true;
    }
}
