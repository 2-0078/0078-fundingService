package com.pieceofcake.fundingservice.funding.infrastructure.client;

import com.pieceofcake.fundingservice.funding.infrastructure.client.dto.CreateBoardRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "board-service",
        url = "${EC2_HOST}:8000/board-service/api/v1")
public interface BoardClient {
    @PostMapping("/board/community")
    void createBoard(@RequestBody CreateBoardRequestDto createBoardRequestDto);
}
