package com.pieceofcake.fundingservice.participation.infrastructure.client;

import com.pieceofcake.fundingservice.participation.infrastructure.client.dto.CreatePaymentRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "payment-service", url = "${EC2_HOST}:8000/payment-service/api/v1/payment")
public interface PaymentClient {
    @GetMapping
    String payment();

    @PostMapping
    String paymentPieces(CreatePaymentRequestDto createPaymentRequestDto);

    @DeleteMapping
    String cancelPayment(CreatePaymentRequestDto createPaymentRequestDto);
}