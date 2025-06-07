package com.pieceofcake.fundingservice.funding.entity;

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
public class WishFunding extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fundingUuid;

    @Column(nullable = false)
    private String memberUuid;

    @Column(nullable = false)
    private String productUuid;
}
