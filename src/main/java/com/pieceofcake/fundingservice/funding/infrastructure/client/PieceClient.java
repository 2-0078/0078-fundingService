package com.pieceofcake.fundingservice.funding.infrastructure.client;

import com.pieceofcake.fundingservice.funding.infrastructure.client.dto.CreatePieceRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "piece-service", url = "${EC2_HOST}:8000/piece-service/api/v1/piece")
public interface PieceClient {
    @PostMapping
    String createPiece(CreatePieceRequestDto createPieceRequestDto);
}
