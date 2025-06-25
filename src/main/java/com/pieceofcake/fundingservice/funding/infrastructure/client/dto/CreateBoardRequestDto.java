package com.pieceofcake.fundingservice.funding.infrastructure.client.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CreateBoardRequestDto {
    private final String boardType = "FUNDING";
    private String boardUuid;

    public CreateBoardRequestDto(String boardUuid) {
        this.boardUuid = boardUuid;
    }
}
