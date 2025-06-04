package com.pieceofcake.fundingservice.entity;

import com.pieceofcake.fundingservice.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FundingParticipation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true ,nullable = false)
    private String participationUuid;

    @Column(nullable = false)
    private String fundingUuid;

    @Column(nullable = false)
    private String memberUuid;

    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private ParticipateStatus participateStatus;
}
