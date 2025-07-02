package com.pieceofcake.fundingservice.participation.application;

import com.pieceofcake.fundingservice.common.application.OutboxService;
import com.pieceofcake.fundingservice.funding.infrastructure.client.PieceClient;
import com.pieceofcake.fundingservice.funding.infrastructure.repository.FundingRepository;
import com.pieceofcake.fundingservice.kafka.producer.FundingKafkaProducer;
import com.pieceofcake.fundingservice.participation.dto.in.ParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.participation.entity.ParticipateStatus;
import com.pieceofcake.fundingservice.participation.infrastructure.FundingParticipationRepository;
import com.pieceofcake.fundingservice.participation.infrastructure.client.PaymentClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundingParticipationServiceImplConcurrencyTest {

    @Mock
    private FundingParticipationRepository participationRepository;
    
    @Mock
    private FundingRepository fundingRepository;
    
    @Mock
    private FundingKafkaProducer fundingKafkaProducer;
    
    @Mock
    private RedisService redisService;
    
    @Mock
    private PaymentClient paymentClient;
    
    @Mock
    private PieceClient pieceClient;
    
    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private FundingParticipationServiceImpl participationService;

    private String fundingUuid;
    private String memberUuid;
    private String productUuid;

    @BeforeEach
    void setUp() {
        fundingUuid = UUID.randomUUID().toString();
        memberUuid = UUID.randomUUID().toString();
        productUuid = UUID.randomUUID().toString();
        
        when(fundingRepository.findProductUuidByFundingUuid(fundingUuid))
                .thenReturn(java.util.Optional.of(productUuid));
        when(redisService.getPiecePrice(fundingUuid)).thenReturn(10000L);
        doNothing().when(paymentClient).createMoney(any());
        doNothing().when(pieceClient).distributePiece(anyString(), any());
        doNothing().when(outboxService).saveEvent(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void participateFunding_ConcurrentRequests_ShouldHandleCorrectly() throws InterruptedException {
        // Given
        int threadCount = 10;
        int totalPieces = 5; // 총 5개 조각만 있음
        int requestQuantity = 1; // 각 요청당 1개씩
        
        // Redis에서 남은 조각 수를 순차적으로 반환 (5, 4, 3, 2, 1, 0, 0, 0, 0, 0)
        AtomicInteger remainingPieces = new AtomicInteger(totalPieces);
        when(redisService.decreaseRemainPieces(eq(fundingUuid), eq(requestQuantity)))
                .thenAnswer(invocation -> {
                    int current = remainingPieces.getAndDecrement();
                    return current > 0 ? current - 1 : 0;
                });
        
        when(participationRepository.save(any())).thenReturn(null);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    ParticipateFundingRequestDto request = ParticipateFundingRequestDto.builder()
                            .fundingUuid(fundingUuid)
                            .memberUuid(memberUuid + threadId)
                            .quantity(requestQuantity)
                            .participateStatus(ParticipateStatus.JOIN)
                            .build();
                    
                    participationService.participateFunding(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // Then
        assertEquals(5, successCount.get()); // 5개 성공
        assertEquals(5, failureCount.get()); // 5개 실패 (재고 부족)
        
        // Redis 호출 검증
        verify(redisService, times(threadCount)).decreaseRemainPieces(eq(fundingUuid), eq(requestQuantity));
        
        // 성공한 요청만 저장됨
        verify(participationRepository, times(5)).save(any());
        verify(paymentClient, times(5)).createMoney(any());
        verify(pieceClient, times(5)).distributePiece(anyString(), any());
        verify(outboxService, times(5)).saveEvent(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void participateFunding_WhenRedisFails_ShouldRollbackCorrectly() {
        // Given
        when(redisService.decreaseRemainPieces(eq(fundingUuid), eq(1)))
                .thenReturn(1L); // Redis는 성공
        when(participationRepository.save(any()))
                .thenThrow(new RuntimeException("Database error")); // DB 저장 실패
        
        // When & Then
        ParticipateFundingRequestDto request = ParticipateFundingRequestDto.builder()
                .fundingUuid(fundingUuid)
                .memberUuid(memberUuid)
                .quantity(1)
                .participateStatus(ParticipateStatus.JOIN)
                .build();
        
        assertThrows(Exception.class, () -> {
            participationService.participateFunding(request);
        });
        
        // Redis 롤백이 호출되어야 함
        verify(redisService, times(1)).increaseRemainPieces(eq(fundingUuid), eq(1));
        
        // 다른 서비스 호출은 되지 않아야 함
        verify(paymentClient, never()).createMoney(any());
        verify(pieceClient, never()).distributePiece(anyString(), any());
        verify(outboxService, never()).saveEvent(anyString(), anyString(), anyString(), anyString());
    }
} 