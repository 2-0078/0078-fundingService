package com.pieceofcake.fundingservice.funding.infrastructure.client;

import com.pieceofcake.fundingservice.common.entity.BaseResponseEntity;
import com.pieceofcake.fundingservice.funding.infrastructure.client.dto.CreatePieceRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "piece-service",
        url = "${EC2_HOST}:8000/piece-service/api/v1")
public interface PieceClient {
    @PostMapping("/piece")
    void createPieces(@RequestBody CreatePieceRequestDto createPieceRequestDto);
}
