package com.pieceofcake.fundingservice.participation.infrastructure.client;

import com.pieceofcake.fundingservice.common.config.FeignConfig;
import com.pieceofcake.fundingservice.common.entity.BaseResponseEntity;
import com.pieceofcake.fundingservice.participation.infrastructure.client.dto.CreatePaymentRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "payment-service",
        url = "${EC2_HOST}:8000/payment-service/api/v1",
        configuration = FeignConfig.class)
public interface PaymentClient {
    @PostMapping("/money/with-member-uuid")
    void createMoney(@RequestBody CreatePaymentRequestDto createPaymentRequestDto);

}