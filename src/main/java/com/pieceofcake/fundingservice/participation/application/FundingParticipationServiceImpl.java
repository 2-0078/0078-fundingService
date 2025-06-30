package com.pieceofcake.fundingservice.participation.application;

import com.pieceofcake.fundingservice.common.entity.BaseResponseStatus;
import com.pieceofcake.fundingservice.common.exception.BaseException;
import com.pieceofcake.fundingservice.funding.infrastructure.client.PieceClient;
import com.pieceofcake.fundingservice.funding.infrastructure.client.dto.DistributePieceRequestDto;
import com.pieceofcake.fundingservice.funding.infrastructure.repository.FundingRepository;
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

    @Override
    @Transactional
    public void participateFunding(ParticipateFundingRequestDto fundingJoinRequestDto) {
        log.info("공모 참여중");
        //productUuid 조회
        String productUuid = String.valueOf(fundingRepository.findProductUuidByFundingUuid(fundingJoinRequestDto.getFundingUuid()));
        log.info("productUuid :  {}", productUuid);

        //레디스에서 처리한 조각 수
        long quantity = redisService.decreaseRemainPieces(
                fundingJoinRequestDto.getFundingUuid(), fundingJoinRequestDto.getQuantity());
        if(quantity == 0){
            throw new BaseException((BaseResponseStatus.NO_MORE_PIECES));
        }
        log.info("quantity :  {}", quantity);

        try {
            log.info("try");
            participationRepository.save(fundingJoinRequestDto.toEntity((int)quantity));
            log.info("저장 완료");
            //결제
            paymentClient.createMoney(CreatePaymentRequestDto.builder()
                            .memberUuid(fundingJoinRequestDto.getMemberUuid())
                            .amount(getPiecePrice(fundingJoinRequestDto.getFundingUuid()) * fundingJoinRequestDto.getQuantity())
                            .isPositive(false)
                            .historyType(MoneyHistoryType.FUNDING)
                            .moneyHistoryDetail(fundingJoinRequestDto.getFundingUuid())
                            .build());
            log.info("결제 완료");

            pieceClient.distributePiece(fundingJoinRequestDto.getMemberUuid(),DistributePieceRequestDto.builder()
                            .productUuid(productUuid)
                            .pieceQuantity(fundingJoinRequestDto.getQuantity())
                            .applyStatus(true)
                            .build());
            log.info("조각 분배 완료");

            //read 남은 조각 update 이벤트 발행
            createRemainPieceEvent(fundingJoinRequestDto.getFundingUuid());
            log.info("남은조각 이벤트 발행 완료");
        }catch (Exception e){
            log.info("롤백");
            redisService.increaseRemainPieces(fundingJoinRequestDto.getFundingUuid(), fundingJoinRequestDto.getQuantity());
            throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR,e);
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

            //read 남은 조각 update 이벤트 발행
            createRemainPieceEvent(cancelDto.getFundingUuid());
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

    private void createRemainPieceEvent(String fundingUuid) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                FundingRemainPieceEvent event = FundingRemainPieceEvent.builder()
                        .fundingUuid(fundingUuid)
                        .remainingPieces(getRemainingPieces(fundingUuid))
                        .build();
                fundingKafkaProducer.sendFundingRemainPieceEvent(event);
            }
        });
    }
}