package com.pieceofcake.fundingservice.kafka.producer;

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
    private final KafkaTemplate<String, RefundEvent> refundKafkaTemplate;
    private final KafkaTemplate<String, FundingRemainPieceEvent> fundingKafkaTemplate;

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

    public void sendRefundEvent(RefundEvent refundEvent) {
        log.info("sendRefundEvent: {}", refundEvent);
        CompletableFuture<SendResult<String, RefundEvent>> future
                = refundKafkaTemplate.send("refund-funding", refundEvent);
    }

    public void sendFundingRemainPieceEvent(FundingRemainPieceEvent remainPieceEvent) {
        log.info("sendFundingRemainPieceEvent: {}", remainPieceEvent);
        CompletableFuture<SendResult<String, FundingRemainPieceEvent>> future
                = fundingKafkaTemplate.send("remain-funding",remainPieceEvent);
    }
}
