package com.pieceofcake.fundingservice.participation.application;

import com.pieceofcake.fundingservice.common.entity.BaseResponseStatus;
import com.pieceofcake.fundingservice.common.exception.BaseException;
import com.pieceofcake.fundingservice.funding.infrastructure.client.PieceClient;
import com.pieceofcake.fundingservice.funding.infrastructure.client.dto.DistributePieceRequestDto;
import com.pieceofcake.fundingservice.funding.infrastructure.repository.FundingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pieceofcake.fundingservice.common.application.OutboxService;
import com.pieceofcake.fundingservice.kafka.producer.FundingKafkaProducer;
import com.pieceofcake.fundingservice.kafka.producer.FundingRemainPieceEvent;
import com.pieceofcake.fundingservice.participation.dto.in.ParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.participation.dto.out.GetParticipateFundingResponseDto;
import com.pieceofcake.fundingservice.participation.entity.ParticipateStatus;
import com.pieceofcake.fundingservice.participation.infrastructure.FundingParticipationRepository;
import com.pieceofcake.fundingservice.participation.entity.FundingParticipation;
import com.pieceofcake.fundingservice.participation.infrastructure.client.PaymentClient;
import com.pieceofcake.fundingservice.participation.infrastructure.client.dto.CreatePaymentRequestDto;
import com.pieceofcake.fundingservice.participation.infrastructure.client.dto.MoneyHistoryType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundingParticipationServiceImpl implements FundingParticipationService {

    private final FundingParticipationRepository participationRepository;
    private final FundingRepository fundingRepository;
    private final FundingKafkaProducer fundingKafkaProducer;
    private final RedisService redisService;
    private final PaymentClient paymentClient;
    private final PieceClient pieceClient;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void participateFunding(ParticipateFundingRequestDto fundingJoinRequestDto) {
        log.info("Funding participation started: fundingUuid={}, memberUuid={}, quantity={}", 
                fundingJoinRequestDto.getFundingUuid(), 
                fundingJoinRequestDto.getMemberUuid(), 
                fundingJoinRequestDto.getQuantity());
        
        // 1. productUuid 조회
        String productUuid = fundingRepository.findProductUuidByFundingUuid(fundingJoinRequestDto.getFundingUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_FUNDING));
        log.debug("Product UUID retrieved: {}", productUuid);

        // 2. Redis에서 재고 감소 (동시성 제어)
        long quantity = redisService.decreaseRemainPieces(
                fundingJoinRequestDto.getFundingUuid(), fundingJoinRequestDto.getQuantity());
        if(quantity == 0){
            log.warn("No more pieces available: fundingUuid={}", fundingJoinRequestDto.getFundingUuid());
            throw new BaseException(BaseResponseStatus.NO_MORE_PIECES);
        }
        log.debug("Remaining pieces after decrease: {}", quantity);

        try {
            // 3. 참여 내역 저장
            log.debug("Saving participation record");
            participationRepository.save(fundingJoinRequestDto.toEntity((int)quantity));
            
            // 4. 결제 처리
            long paymentAmount = getPiecePrice(fundingJoinRequestDto.getFundingUuid()) * fundingJoinRequestDto.getQuantity();
            log.debug("Processing payment: amount={}", paymentAmount);
            paymentClient.createMoney(CreatePaymentRequestDto.builder()
                            .memberUuid(fundingJoinRequestDto.getMemberUuid())
                            .amount(paymentAmount)
                            .isPositive(false)
                            .historyType(MoneyHistoryType.FUNDING)
                            .moneyHistoryDetail(fundingJoinRequestDto.getFundingUuid())
                            .build());

            // 5. 조각 분배
            try{
                log.debug("Distributing pieces to member");
                pieceClient.distributePiece(fundingJoinRequestDto.getMemberUuid(),DistributePieceRequestDto.builder()
                        .productUuid(productUuid)
                        .pieceQuantity(fundingJoinRequestDto.getQuantity())
                        .applyStatus(true)
                        .build());
            }catch (Exception e){
                log.error("Failed to distribute pieces, initiating refund: memberUuid={}, error={}", 
                        fundingJoinRequestDto.getMemberUuid(), e.getMessage(), e);
                // 조각 분배 실패 시 환불
                paymentClient.createMoney(CreatePaymentRequestDto.builder()
                        .memberUuid(fundingJoinRequestDto.getMemberUuid())
                        .amount(paymentAmount)
                        .isPositive(true)
                        .historyType(MoneyHistoryType.REFUND)
                        .moneyHistoryDetail(fundingJoinRequestDto.getFundingUuid())
                        .build());
            }

            // 6. Outbox 패턴으로 남은 조각 이벤트 저장
            log.debug("Saving remain piece event to outbox");
            saveRemainPieceEventToOutbox(fundingJoinRequestDto.getFundingUuid());
            
            log.info("Funding participation completed successfully: fundingUuid={}, memberUuid={}", 
                    fundingJoinRequestDto.getFundingUuid(), fundingJoinRequestDto.getMemberUuid());
        }catch (Exception e){
            log.error("Funding participation failed, rolling back Redis: fundingUuid={}, memberUuid={}, error={}", 
                    fundingJoinRequestDto.getFundingUuid(), fundingJoinRequestDto.getMemberUuid(), e.getMessage(), e);
            // 롤백: Redis 재고 복구
            redisService.increaseRemainPieces(fundingJoinRequestDto.getFundingUuid(), fundingJoinRequestDto.getQuantity());
            throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    @Override
    @Transactional
    public void cancelParticipation(ParticipateFundingRequestDto cancelDto) {
        int totalQuantity = getMyTotalParticipationQuantity(cancelDto);
        if(totalQuantity == 0){
            throw new BaseException(BaseResponseStatus.NO_PARTICIPATION_HISTORY);
        }
        if(!redisService.increaseRemainPieces(cancelDto.getFundingUuid(), totalQuantity)){
            throw new BaseException(BaseResponseStatus.CANNOT_CANCEL_PARTICIPATION);
        }
        try{
            participationRepository.save(cancelDto.toEntity(totalQuantity));
            //환불
            paymentClient.createMoney(CreatePaymentRequestDto.builder()
                    .amount(getPiecePrice(cancelDto.getFundingUuid()) * totalQuantity)
                    .isPositive(true)
                    .historyType(MoneyHistoryType.REFUND)
                    .moneyHistoryDetail(cancelDto.getFundingUuid()+"- 공모 취소")
                    .build());

            // 6. Outbox 패턴으로 남은 조각 이벤트 저장
            log.debug("Saving remain piece event to outbox");
            saveRemainPieceEventToOutbox(cancelDto.getFundingUuid());
        }catch (Exception e){
            redisService.increaseRemainPieces(cancelDto.getFundingUuid(), totalQuantity);
            throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR,e);
        }
    }

    @Override
    public GetParticipateFundingResponseDto getMyFundingParticipations(String fundingUuid, String memberUuid) {

        int totalQuantity = participationRepository.findByFundingUuidAndMemberUuidAndParticipateStatus(fundingUuid, memberUuid,ParticipateStatus.JOIN)
                .stream()
                .mapToInt(FundingParticipation::getQuantity)
                .sum();

        return GetParticipateFundingResponseDto.builder()
                .fundingUuid(fundingUuid)
                .memberUuid(memberUuid)
                .quantity(totalQuantity)
                .build();
    }

    @Override
    public boolean getMyFunding(ParticipateFundingRequestDto fundingJoinRequestDto) {
        return getMyTotalParticipationQuantity(
                fundingJoinRequestDto) > 0;
    }

    @Override
    public int getMyTotalParticipationQuantity(ParticipateFundingRequestDto participateFundingRequestDto) {
        return participationRepository.getJoinMinusCancelCount(participateFundingRequestDto.getFundingUuid(),
                participateFundingRequestDto.getMemberUuid());
    }

    @Override
    public int getRemainingPieces(String fundingUuid) {
        return redisService.getRemainingPieces(fundingUuid);
    }

    private long getPiecePrice(String fundingUuid) {
        return redisService.getPiecePrice(fundingUuid);
    }

    private void saveRemainPieceEventToOutbox(String fundingUuid) {
        try {
            FundingRemainPieceEvent event = FundingRemainPieceEvent.builder()
                    .fundingUuid(fundingUuid)
                    .remainingPieces(getRemainingPieces(fundingUuid))
                    .build();
            
            String payload = objectMapper.writeValueAsString(event);
            outboxService.saveEvent("FUNDING_REMAIN_PIECE", "FUNDING", fundingUuid, payload);
            
            log.debug("Remain piece event saved to outbox: fundingUuid={}", fundingUuid);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize remain piece event: fundingUuid={}, error={}", 
                    fundingUuid, e.getMessage(), e);
            throw new RuntimeException("Failed to serialize remain piece event", e);
        }
    }
}