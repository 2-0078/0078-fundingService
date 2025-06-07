package com.pieceofcake.fundingservice.funding.entity;

import com.pieceofcake.fundingservice.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor //쓰지말기
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
    private Double fundingAmount;

    @Column(name = "piece_price", nullable = false)
    private Double piecePrice;

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

    public void updateFundingStatus(FundingStatus fundingStatus){
        this.fundingStatus = fundingStatus;
    }

    //참여자는 많음 => 재고관리 분리 / 재고만 관리하는 집계테이블(레디스) 소진시 이벤트
    public void increaseRemainingPieces(Integer quantity){this.remainingPieces += quantity;}

    public void decreaseRemainingPieces(Integer quantity){this.remainingPieces -= quantity;}

    public void deleteFunding(){
        this.isDeleted = true;
    }
}
