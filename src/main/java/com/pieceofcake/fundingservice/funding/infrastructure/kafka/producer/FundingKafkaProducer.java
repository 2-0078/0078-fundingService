package com.pieceofcake.fundingservice.funding.infrastructure.kafka.producer;

import org.springframework.kafka.support.SendResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundingKafkaProducer {
    private final KafkaTemplate<String, FundingEvent> kafkaTemplate;

    public void sendCreateFundingEvent(FundingEvent fundingEvent) {
        log.info("sendFundingEvent: {}", fundingEvent);
        CompletableFuture<SendResult<String, FundingEvent>> future
                = kafkaTemplate.send("create-funding", fundingEvent);
    }

    public void sendDeleteFundingEvent(FundingEvent fundingEvent) {
        log.info("sendFundingEvent: {}", fundingEvent);
        CompletableFuture<SendResult<String, FundingEvent>> future
                = kafkaTemplate.send("delete-funding", fundingEvent);
    }
}
