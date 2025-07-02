package com.pieceofcake.fundingservice.common.infrastructure;

import com.pieceofcake.fundingservice.common.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    
    @Query("SELECT o FROM OutboxEvent o WHERE o.status = 'PENDING' ORDER BY o.createdAt ASC")
    List<OutboxEvent> findPendingEvents();
    
    @Query("SELECT o FROM OutboxEvent o WHERE o.status = 'FAILED' AND (o.retryCount IS NULL OR o.retryCount < :maxRetries) ORDER BY o.createdAt ASC")
    List<OutboxEvent> findFailedEventsWithRetry(@Param("maxRetries") int maxRetries);
    
    @Query("SELECT o FROM OutboxEvent o WHERE o.aggregateType = :aggregateType AND o.aggregateId = :aggregateId ORDER BY o.createdAt DESC")
    List<OutboxEvent> findByAggregate(@Param("aggregateType") String aggregateType, @Param("aggregateId") String aggregateId);
} 