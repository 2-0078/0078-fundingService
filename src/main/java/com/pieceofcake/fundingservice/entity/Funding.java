package com.pieceofcake.fundingservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Funding extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String fundingUuid;

    @Column(unique = true, nullable = false)
    private String productUuid;

    @Column( nullable = false)
    private Long fundingAmount;

    @Column( nullable = false)
    private Long piecePrice;

    @Column( nullable = false)
    private Integer totalPieces;

    @Column( nullable = false)
    private Integer remainingPieces;

    @Column( nullable = false)
    private LocalDateTime fundingDeadline;

    @Column( nullable = false)
    private FundingStatus fundingStatus;

    private Boolean isDeleted;

    public void updateFundingStatus(FundingStatus fundingStatus){
        this.fundingStatus = fundingStatus;
    }

    public void deleteFunding(){
        this.isDeleted = true;
    }
}
