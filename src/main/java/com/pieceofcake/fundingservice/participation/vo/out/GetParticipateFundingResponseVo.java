package com.pieceofcake.fundingservice.participation.vo.out;

import com.pieceofcake.fundingservice.participation.entity.ParticipateStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class GetParticipateFundingResponseVo {
    private String participationUuid;
    private String fundingUuid;
    private String memberUuid;
    private Integer quantity;
    private ParticipateStatus fundingStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
