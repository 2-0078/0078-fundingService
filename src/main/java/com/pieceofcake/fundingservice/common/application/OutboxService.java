package com.pieceofcake.fundingservice.common.application;

import com.pieceofcake.fundingservice.common.entity.OutboxEvent;

public interface OutboxService {
    void saveEvent(String eventType, String aggregateType, String aggregateId, String payload);
    void processPendingEvents();
    void processFailedEvents();
} 