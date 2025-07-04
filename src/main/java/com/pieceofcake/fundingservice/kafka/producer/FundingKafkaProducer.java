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
    private final KafkaTemplate<String, CompletedFundingEvent> completedKafkaTemplate;
    private final KafkaTemplate<String, AlertEvent> alertKafkaTemplate;

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

    public void sendCompleteFundingEvent(CompletedFundingEvent fundingEvent) {
        log.info("sendCompleteFundingEvent: {}", fundingEvent);
        CompletableFuture<SendResult<String, CompletedFundingEvent>> future
                = completedKafkaTemplate.send("complete-funding", fundingEvent);
    }

    //알람 서비스 - 공모 조각 수 변경(공용)
    public void sendRemainPiecesAlertEvent(AlertEvent alertEvent) {
        log.info("sendRemainPiecesAlertEvent: {}", alertEvent);
        CompletableFuture<SendResult<String, AlertEvent>> future
                = alertKafkaTemplate.send("update-funding-piece-count-alarm", alertEvent);
    }

    //알람 서비스 - 공모 시작(FUNDING)
    public void sendOpenFundingAlertEvent(AlertEvent alertEvent) {
        log.info("sendOpenFundingAlertEvent: {}", alertEvent);
        CompletableFuture<SendResult<String, AlertEvent>> future
                = alertKafkaTemplate.send("start-funding-alarm", alertEvent);
    }

    //알람 서비스 - 공모 완료 (완료/취소)
    public void sendClosedFundingAlertEvent(AlertEvent alertEvent) {
        log.info("sendClosedFundingAlertEvent: {}", alertEvent);
        CompletableFuture<SendResult<String, AlertEvent>> future
                = alertKafkaTemplate.send("end-funding-alarm", alertEvent);
    }

}
