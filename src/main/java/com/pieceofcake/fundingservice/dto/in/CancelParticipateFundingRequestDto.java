package com.pieceofcake.fundingservice.dto.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelParticipateFundingRequestDto {
    private String fundingUuid;
    private String memberUuid;

    public static CancelParticipateFundingRequestDto from(String fundingUuid, String memberUuid) {
        return CancelParticipateFundingRequestDto.builder()
                .fundingUuid(fundingUuid)
                .memberUuid(memberUuid)
                .build();
    }
}
