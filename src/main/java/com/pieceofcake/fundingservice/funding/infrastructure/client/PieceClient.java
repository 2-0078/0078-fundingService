package com.pieceofcake.fundingservice.funding.infrastructure.client;

import com.pieceofcake.fundingservice.common.config.FeignConfig;
import com.pieceofcake.fundingservice.funding.infrastructure.client.dto.CreatePieceRequestDto;
import com.pieceofcake.fundingservice.funding.infrastructure.client.dto.DistributePieceRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "piece-service",
        url = "${EC2_HOST}:8000/piece-service/api/v1",
        configuration = FeignConfig.class)
public interface PieceClient {
    @PostMapping("/piece")
    void createPieces(@RequestBody CreatePieceRequestDto createPieceRequestDto);

    @PutMapping("/piece/apply")
    void applyPiece(
            @RequestHeader("X-Member-Uuid") String memberUuid,
            @RequestBody DistributePieceRequestDto distributePieceRequestDto);

    @PutMapping("/piece/cancel")
    void cancelPiece(
            @RequestHeader("X-Member-Uuid") String memberUuid,
            @RequestBody DistributePieceRequestDto distributePieceRequestDto);
}
