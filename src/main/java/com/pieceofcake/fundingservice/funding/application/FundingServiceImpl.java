package com.pieceofcake.fundingservice.funding.application;

import com.pieceofcake.fundingservice.common.entity.BaseResponseStatus;
import com.pieceofcake.fundingservice.common.exception.BaseException;
import com.pieceofcake.fundingservice.funding.dto.in.*;
import com.pieceofcake.fundingservice.funding.dto.out.GetFundingResponseDto;
import com.pieceofcake.fundingservice.funding.dto.out.GetWishFundingResponseDto;
import com.pieceofcake.fundingservice.funding.entity.Funding;
import com.pieceofcake.fundingservice.funding.entity.FundingStatus;
import com.pieceofcake.fundingservice.funding.infrastructure.client.PieceClient;
import com.pieceofcake.fundingservice.funding.infrastructure.client.dto.CreatePieceRequestDto;
import com.pieceofcake.fundingservice.funding.infrastructure.kafka.producer.FundingEvent;
import com.pieceofcake.fundingservice.funding.infrastructure.kafka.producer.FundingKafkaProducer;
import com.pieceofcake.fundingservice.funding.infrastructure.repository.FundingRepository;
import com.pieceofcake.fundingservice.funding.infrastructure.repository.WishFundingRepository;
import com.pieceofcake.fundingservice.participation.application.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
public class FundingServiceImpl implements FundingService {

    private final FundingRepository fundingRepository;
    private final WishFundingRepository wishFundingRepository;
    private final RedisService redisService;
    private final PieceClient pieceClient;
    private final FundingKafkaProducer fundingKafkaProducer;

    /*
    * 상품명, 카테고리, (최신/가격/남은조각 수) 정렬
    * */
    @Override
    public List<String> getFundingUuidList() {
        return fundingRepository.findAll().stream().map(Funding::getFundingUuid).toList();
    }

    @Override
    public GetFundingResponseDto getFunding(String fundingUuid) {
        return GetFundingResponseDto.from(fundingRepository.findByFundingUuid(fundingUuid)
                .orElseThrow(()-> new IllegalArgumentException("no funding")) );
    }

    /*
    * 공모 등록시에는 Status : READY -> 조각 발행 X
    * */
    @Override
    @Transactional
    public void createFunding(CreateFundingRequestDto createFundingRequestDto) {
        try {
            redisService.setRemainingPieces(
                    SetRedisFundingRequestDto.builder()
                            .fundingUuid(createFundingRequestDto.getFundingUuid())
                            .totalPieces(createFundingRequestDto.getTotalPieces())
                            .remainingPieces(createFundingRequestDto.getRemainingPieces())
                            .piecePrice(createFundingRequestDto.getPiecePrice())
                            .build()
            );
            Funding saved = fundingRepository.save(createFundingRequestDto.toEntity());

            //카프카 이벤트 발행
            createFundingEvent(saved);
        }catch (Exception e){
            throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR,e);
        }
    }

    @Override
    @Transactional
    public void updateFunding(UpdateFundingRequestDto updateFundingRequestDto) {
        Funding entity = fundingRepository.findByFundingUuid(updateFundingRequestDto.getFundingUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_FUNDING));

        if(entity.getFundingStatus() != FundingStatus.READY){
            throw new BaseException(BaseResponseStatus.FAILED_TO_UPDATE);
        }
        //레디스 설정 초기화
        redisService.setRemainingPieces(
                SetRedisFundingRequestDto.builder()
                        .fundingUuid(updateFundingRequestDto.getFundingUuid())
                        .totalPieces(updateFundingRequestDto.getTotalPieces())
                        .remainingPieces(updateFundingRequestDto.getRemainingPieces())
                        .piecePrice(updateFundingRequestDto.getPiecePrice())
                        .build()
        );
        Funding saved = fundingRepository.save(updateFundingRequestDto.toEntity(entity));
        //조각 발행
        if(updateFundingRequestDto.getFundingStatus() == FundingStatus.FUNDING){
            createPieces(saved.getProductUuid(), saved.getTotalPieces());
        }

        //카프카 이벤트 발행
        createFundingEvent(saved);

    }

    @Override
    @Transactional
    public void updateFundingStatus(UpdateFundingRequestDto updateFundingRequestDto) {
        Funding entity = fundingRepository.findByFundingUuid(updateFundingRequestDto.getFundingUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_FUNDING));

        FundingStatus current = entity.getFundingStatus();
        FundingStatus target = updateFundingRequestDto.getFundingStatus();
        if ((current == FundingStatus.READY && target == FundingStatus.FUNDING) ||
                (current == FundingStatus.FUNDING && (target == FundingStatus.COMPLETED || target == FundingStatus.CANCELLED))) {
            entity.updateFundingStatus(target);
        } else {
            throw new IllegalStateException("허용되지 않은 상태 변경: " + current + " → " + target);
        }

        //조각 발행
        if(updateFundingRequestDto.getFundingStatus() == FundingStatus.FUNDING){
            createPieces(entity.getProductUuid(), entity.getTotalPieces());
        }

        //카프카 이벤트 발행
        createFundingEvent(entity);

    }

    @Override
    @Transactional
    public void deleteFunding(String fundingUuid) {
        Funding entity = fundingRepository.findByFundingUuid(fundingUuid)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_FUNDING));

        if(entity.getFundingStatus() == FundingStatus.FUNDING){
            throw new BaseException(BaseResponseStatus.FAILED_TO_UPDATE);
        }
        entity.deleteFunding();

        deleteFundingEvent(entity);
    }


    /*
    * 남은 수량 조회, 0이면 throw 조각 없음 오류
    * 남은 수량 - 주문수량(비관적락), 계산 결과가 0보다 작으면 남은 수량이 0이 될때까지의 값만 처리(일부만 조각 결제)
    * 참여 내역 저장
    * */
//    @Override
//    @Transactional
//    public void participateFunding(ParticipateFundingRequestDto fundingJoinRequestDto) {
//        //수량 조회
//        Funding funding = fundingRepository.findByFundingUuidWithLock(fundingJoinRequestDto.getFundingUuid())
//                .orElseThrow(()-> new BaseException(BaseResponseStatus.NO_EXIST_FUNDING));
//        if(funding.getRemainingPieces() == 0){
//            throw new BaseException(BaseResponseStatus.NO_MORE_PIECES);
//        }
//
//        //결제 서비스
//        //조각 서비스
//
//        //수량 차감
//        funding.decreaseRemainingPieces(fundingJoinRequestDto.getQuantity());
//
//        //참여내역 저장
//        participationService.joinFunding(fundingJoinRequestDto); //롤백 문제-> 카프카
//
//        //레디스 (대기번호 실시간으로)-> 차감 -> 히스토리 저장
//        //샤딩 -> 제이미터 테스트
//        // 재고관리ㅣ 서비스 따로  rabbit mq
//    }

//    @Override
//    @Transactional
//    public void cancelFunding(CancelParticipateFundingRequestDto cancelDto) {
//        //참여내역 총합 조회
//        int totalQuantity = participationService.getMyTotalParticipationQuantity(cancelDto.getFundingUuid(), cancelDto.getMemberUuid());
//        //공모 불러오기
//        Funding funding = fundingRepository.findByFundingUuidWithLock(cancelDto.getFundingUuid())
//                .orElseThrow(()-> new BaseException(BaseResponseStatus.NO_EXIST_FUNDING));
//        //남은 조각 증가
//        funding.increaseRemainingPieces(totalQuantity);
//        //참여내역 cancel
//        participationService.cancelParticipation(cancelDto.getFundingUuid(), cancelDto.getMemberUuid());
////취소내역 create 해야함
//        //결제 서비스 - 환불
//        //조각 서비스 - 조각 상태 변경
//    }

    @Override
    public int getRemainingPieces(String fundingUuid) { //실시간으로?
        return fundingRepository.findByFundingUuid(fundingUuid).orElseThrow(
                ()-> new BaseException(BaseResponseStatus.NO_EXIST_FUNDING)
        ).getRemainingPieces();
    }

    @Override
    public List<GetWishFundingResponseDto> getWishFundingList(String memberUuid) {
        return wishFundingRepository.getByMemberUuid(memberUuid).stream().map(GetWishFundingResponseDto::from).toList();
    }

    @Override
    public Boolean isWishFunding(String fundingUuid, String memberUuid) {
        return wishFundingRepository.existsByMemberUuidAndFundingUuid(memberUuid,fundingUuid);
    }

    @Override
    public void wishFunding(CreateWishFundingRequestDto createWishFundingRequestDto) {
        wishFundingRepository.save(createWishFundingRequestDto.toEntity());
    }//참여 취소 토글 형태

    @Override
    public void cancelWishFunding(Long id) {
        wishFundingRepository.deleteById(id);
    }

    private void createPieces(String productUuid, int totalPieces){
        //조각 발행
        pieceClient.createPieces(CreatePieceRequestDto.builder()
                .productUuid(productUuid)
                .totalPieces(totalPieces)
                .build());
    }

    private void createFundingEvent(Funding entity){
        //카프카 이벤트 발행
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                FundingEvent event = FundingEvent.builder()
                        .fundingUuid(entity.getFundingUuid())
                        .productUuid(entity.getProductUuid())
                        .totalPieces(entity.getTotalPieces())
                        .remainingPieces(entity.getRemainingPieces())
                        .piecePrice(entity.getPiecePrice())
                        .fundingAmount(entity.getFundingAmount())
                        .fundingDeadline(entity.getFundingDeadline().toString())
                        .fundingStatus(entity.getFundingStatus().toString())
                        .build();
                fundingKafkaProducer.sendCreateFundingEvent(event);
            }
        });
    }

    private void deleteFundingEvent(Funding entity){
        //카프카 이벤트 발행
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                FundingEvent event = FundingEvent.builder()
                        .productUuid(entity.getProductUuid())
                        .build();
                fundingKafkaProducer.sendDeleteFundingEvent(event);
            }
        });
    }
}
