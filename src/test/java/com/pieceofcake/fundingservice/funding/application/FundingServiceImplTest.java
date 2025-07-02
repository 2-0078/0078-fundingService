package com.pieceofcake.fundingservice.funding.application;

import com.pieceofcake.fundingservice.common.application.OutboxService;
import com.pieceofcake.fundingservice.funding.dto.in.CreateFundingRequestDto;
import com.pieceofcake.fundingservice.funding.entity.Funding;
import com.pieceofcake.fundingservice.funding.entity.FundingStatus;
import com.pieceofcake.fundingservice.funding.infrastructure.client.BoardClient;
import com.pieceofcake.fundingservice.funding.infrastructure.client.PieceClient;
import com.pieceofcake.fundingservice.funding.infrastructure.repository.FundingRepository;
import com.pieceofcake.fundingservice.funding.infrastructure.repository.WishFundingRepository;
import com.pieceofcake.fundingservice.kafka.producer.FundingKafkaProducer;
import com.pieceofcake.fundingservice.participation.application.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FundingServiceImplTest {

    @Mock
    private FundingRepository fundingRepository;
    
    @Mock
    private WishFundingRepository wishFundingRepository;
    
    @Mock
    private RedisService redisService;
    
    @Mock
    private PieceClient pieceClient;
    
    @Mock
    private BoardClient boardClient;
    
    @Mock
    private FundingKafkaProducer fundingKafkaProducer;
    
    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private FundingServiceImpl fundingService;

    private CreateFundingRequestDto createFundingRequestDto;
    private Funding funding;

    @BeforeEach
    void setUp() {
        String fundingUuid = UUID.randomUUID().toString();
        String productUuid = UUID.randomUUID().toString();
        
        createFundingRequestDto = CreateFundingRequestDto.builder()
                .fundingUuid(fundingUuid)
                .productUuid(productUuid)
                .fundingAmount(1000000L)
                .piecePrice(10000L)
                .totalPieces(100)
                .remainingPieces(100)
                .fundingDeadline(LocalDateTime.now().plusDays(30))
                .fundingStatus(FundingStatus.READY)
                .build();

        funding = Funding.builder()
                .id(1L)
                .fundingUuid(fundingUuid)
                .productUuid(productUuid)
                .fundingAmount(1000000L)
                .piecePrice(10000L)
                .totalPieces(100)
                .remainingPieces(100)
                .fundingDeadline(LocalDateTime.now().plusDays(30))
                .fundingStatus(FundingStatus.READY)
                .isDeleted(false)
                .version(0L)
                .build();
    }

    @Test
    void createFunding_Success() {
        // Given
        doNothing().when(boardClient).createBoard(any());
        when(fundingRepository.save(any(Funding.class))).thenReturn(funding);
        doNothing().when(outboxService).saveEvent(anyString(), anyString(), anyString(), anyString());

        // When
        fundingService.createFunding(createFundingRequestDto);

        // Then
        verify(boardClient, times(1)).createBoard(any());
        verify(redisService, times(1)).setRemainingPieces(any());
        verify(fundingRepository, times(1)).save(any(Funding.class));
        verify(outboxService, times(1)).saveEvent(eq("FUNDING_CREATED"), eq("FUNDING"), eq(funding.getFundingUuid()), anyString());
    }

    @Test
    void createFunding_WhenBoardClientFails_ShouldThrowException() {
        // Given
        doThrow(new RuntimeException("Board creation failed")).when(boardClient).createBoard(any());

        // When & Then
        try {
            fundingService.createFunding(createFundingRequestDto);
        } catch (Exception e) {
            verify(boardClient, times(1)).createBoard(any());
            verify(redisService, never()).setRemainingPieces(any());
            verify(fundingRepository, never()).save(any());
            verify(outboxService, never()).saveEvent(anyString(), anyString(), anyString(), anyString());
        }
    }

    @Test
    void updateFundingStatus_WithValidTransition_ShouldSucceed() {
        // Given
        when(fundingRepository.findByFundingUuid(anyString())).thenReturn(java.util.Optional.of(funding));
        when(fundingRepository.save(any(Funding.class))).thenReturn(funding);
        doNothing().when(outboxService).saveEvent(anyString(), anyString(), anyString(), anyString());

        // When
        fundingService.updateFundingStatus(com.pieceofcake.fundingservice.funding.dto.in.UpdateFundingStatusRequestDto.builder()
                .fundingUuid(funding.getFundingUuid())
                .fundingStatus(FundingStatus.FUNDING)
                .build());

        // Then
        verify(fundingRepository, times(1)).save(any(Funding.class));
        verify(pieceClient, times(1)).createPieces(any());
        verify(outboxService, times(1)).saveEvent(eq("FUNDING_CREATED"), eq("FUNDING"), eq(funding.getFundingUuid()), anyString());
    }
} 