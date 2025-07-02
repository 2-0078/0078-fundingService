package com.pieceofcake.fundingservice.common.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pieceofcake.fundingservice.common.entity.OutboxEvent;
import com.pieceofcake.fundingservice.common.infrastructure.OutboxEventRepository;
import com.pieceofcake.fundingservice.kafka.producer.FundingKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final FundingKafkaProducer fundingKafkaProducer;
    private final ObjectMapper objectMapper;

    @Value("${outbox.max-retries:3}")
    private int maxRetries;

    @Override
    @Transactional
    public void saveEvent(String eventType, String aggregateType, String aggregateId, String payload) {
        OutboxEvent event = OutboxEvent.builder()
                .eventType(eventType)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .payload(payload)
                .status(OutboxEvent.OutboxStatus.PENDING)
                .build();
        
        outboxEventRepository.save(event);
        log.info("Outbox event saved: eventType={}, aggregateType={}, aggregateId={}", 
                eventType, aggregateType, aggregateId);
    }

    @Override
    @Scheduled(fixedDelay = 5000) // 5초마다 실행
    @Transactional
    public void processPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findPendingEvents();
        
        for (OutboxEvent event : pendingEvents) {
            try {
                processEvent(event);
                event.markAsProcessed();
                outboxEventRepository.save(event);
                log.info("Event processed successfully: eventId={}, eventType={}", 
                        event.getId(), event.getEventType());
            } catch (Exception e) {
                log.error("Failed to process event: eventId={}, eventType={}, error={}", 
                        event.getId(), event.getEventType(), e.getMessage(), e);
                event.markAsFailed(e.getMessage());
                outboxEventRepository.save(event);
            }
        }
    }

    @Override
    @Scheduled(fixedDelay = 30000) // 30초마다 실행
    @Transactional
    public void processFailedEvents() {
        List<OutboxEvent> failedEvents = outboxEventRepository.findFailedEventsWithRetry(maxRetries);
        
        for (OutboxEvent event : failedEvents) {
            try {
                processEvent(event);
                event.markAsProcessed();
                outboxEventRepository.save(event);
                log.info("Failed event retry successful: eventId={}, eventType={}", 
                        event.getId(), event.getEventType());
            } catch (Exception e) {
                log.error("Failed event retry failed: eventId={}, eventType={}, retryCount={}, error={}", 
                        event.getId(), event.getEventType(), event.getRetryCount(), e.getMessage(), e);
                event.incrementRetryCount();
                outboxEventRepository.save(event);
            }
        }
    }

    private void processEvent(OutboxEvent event) throws JsonProcessingException {
        switch (event.getEventType()) {
            case "FUNDING_CREATED":
                var fundingEvent = objectMapper.readValue(event.getPayload(), 
                        com.pieceofcake.fundingservice.kafka.producer.FundingEvent.class);
                fundingKafkaProducer.sendCreateFundingEvent(fundingEvent);
                break;
            case "FUNDING_DELETED":
                var deleteEvent = objectMapper.readValue(event.getPayload(), 
                        com.pieceofcake.fundingservice.kafka.producer.FundingEvent.class);
                fundingKafkaProducer.sendDeleteFundingEvent(deleteEvent);
                break;
            case "FUNDING_REMAIN_PIECE":
                var remainEvent = objectMapper.readValue(event.getPayload(), 
                        com.pieceofcake.fundingservice.kafka.producer.FundingRemainPieceEvent.class);
                fundingKafkaProducer.sendFundingRemainPieceEvent(remainEvent);
                break;
            case "FUNDING_COMPLETED":
                var completedEvent = objectMapper.readValue(event.getPayload(), 
                        com.pieceofcake.fundingservice.kafka.producer.CompletedFundingEvent.class);
                fundingKafkaProducer.sendCompleteFundingEvent(completedEvent);
                break;
            case "REFUND":
                var refundEvent = objectMapper.readValue(event.getPayload(), 
                        com.pieceofcake.fundingservice.kafka.producer.RefundEvent.class);
                fundingKafkaProducer.sendRefundEvent(refundEvent);
                break;
            default:
                throw new IllegalArgumentException("Unknown event type: " + event.getEventType());
        }
    }
} 