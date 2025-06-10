package com.pieceofcake.fundingservice.participation.application;

import com.pieceofcake.fundingservice.common.entity.BaseResponseStatus;
import com.pieceofcake.fundingservice.common.exception.BaseException;
import com.pieceofcake.fundingservice.participation.dto.in.CancelParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.participation.dto.in.ParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.participation.dto.out.GetParticipateFundingResponseDto;
import com.pieceofcake.fundingservice.participation.entity.ParticipateStatus;
import com.pieceofcake.fundingservice.participation.infrastructure.FundingParticipationRepository;
import com.pieceofcake.fundingservice.participation.entity.FundingParticipation;
import com.pieceofcake.fundingservice.participation.infrastructure.client.PaymentClient;
import com.pieceofcake.fundingservice.participation.infrastructure.client.dto.CreatePaymentRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundingParticipationServiceImpl implements FundingParticipationService {

    private final FundingParticipationRepository participationRepository;
    private final RedisService redisService;
    private final PaymentClient paymentClient;

    @Override
    @Transactional
    public void participateFunding(ParticipateFundingRequestDto fundingJoinRequestDto) {
        //레디스에서 처리한 조각 수
        if(redisService.decreaseRemainPieces(
                fundingJoinRequestDto.getFundingUuid(), fundingJoinRequestDto.getQuantity()) == 0){
            throw new BaseException((BaseResponseStatus.NO_MORE_PIECES));
        }
        try {
            participationRepository.save(fundingJoinRequestDto.toEntity());
            //결제
            paymentClient.paymentPieces(CreatePaymentRequestDto.builder()
                            .fundingUuid(fundingJoinRequestDto.getFundingUuid())
                            .memberUuid(fundingJoinRequestDto.getMemberUuid())
                            .totalPrice(getPiecePrice(fundingJoinRequestDto.getFundingUuid()) * fundingJoinRequestDto.getQuantity())
                            .status(fundingJoinRequestDto.getParticipateStatus().toString())
                            .build());

        }catch (Exception e){
            redisService.increaseRemainPieces(fundingJoinRequestDto.getFundingUuid(), fundingJoinRequestDto.getQuantity());
            throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR,e);
        }
    }

    @Override
    public void cancelParticipation(ParticipateFundingRequestDto cancelDto) {
        int totalQuantity = getMyTotalParticipationQuantity(cancelDto);

        if(!redisService.increaseRemainPieces(cancelDto.getFundingUuid(), totalQuantity)){
            throw new BaseException(BaseResponseStatus.CANNOT_CANCEL_PARTICIPATION);
        }
        try{
            participationRepository.save(cancelDto.toEntity(totalQuantity));
            //환불
            paymentClient.cancelPayment(CreatePaymentRequestDto.builder()
                    .fundingUuid(cancelDto.getFundingUuid())
                    .memberUuid(cancelDto.getMemberUuid())
                    .totalPrice( getPiecePrice(cancelDto.getFundingUuid()) * totalQuantity)
                    .status(cancelDto.getParticipateStatus().toString())
                    .build());
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

    private double getPiecePrice(String fundingUuid) {
        return redisService.getPiecePrice(fundingUuid);
    }
}