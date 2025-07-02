package com.pieceofcake.fundingservice.funding.application;

import com.pieceofcake.fundingservice.common.entity.BaseResponseStatus;
import com.pieceofcake.fundingservice.common.exception.BaseException;
import com.pieceofcake.fundingservice.funding.dto.in.*;
import com.pieceofcake.fundingservice.funding.dto.out.GetFundingResponseDto;
import com.pieceofcake.fundingservice.funding.dto.out.GetWishFundingResponseDto;
import com.pieceofcake.fundingservice.funding.entity.Funding;
import com.pieceofcake.fundingservice.funding.entity.FundingStatus;
import com.pieceofcake.fundingservice.funding.entity.WishFunding;
import com.pieceofcake.fundingservice.funding.infrastructure.client.BoardClient;
import com.pieceofcake.fundingservice.funding.infrastructure.client.PieceClient;
import com.pieceofcake.fundingservice.funding.infrastructure.client.dto.CreateBoardRequestDto;
import com.pieceofcake.fundingservice.funding.infrastructure.client.dto.CreatePieceRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pieceofcake.fundingservice.common.application.OutboxService;
import com.pieceofcake.fundingservice.kafka.producer.FundingEvent;
import com.pieceofcake.fundingservice.kafka.producer.FundingKafkaProducer;
import com.pieceofcake.fundingservice.funding.infrastructure.repository.FundingRepository;
import com.pieceofcake.fundingservice.funding.infrastructure.repository.WishFundingRepository;
import com.pieceofcake.fundingservice.participation.application.RedisService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class FundingServiceImpl implements FundingService {

    private final FundingRepository fundingRepository;
    private final WishFundingRepository wishFundingRepository;
    private final RedisService redisService;
    private final PieceClient pieceClient;
    private final BoardClient boardClient;
    private final FundingKafkaProducer fundingKafkaProducer;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;


    @Override
    public List<GetFundingResponseDto> getFundingList(FundingStatus status) {
        return fundingRepository.findByFundingStatus(status).stream().map(GetFundingResponseDto::from).toList();
    }

    @Override
    public Page<GetFundingResponseDto> getFundingListWithPaging(FundingStatus status, Pageable pageable) {
        Page<Funding> fundingPage;
        if (status != null) {
            fundingPage = fundingRepository.findByFundingStatus(status, pageable);
        } else {
            fundingPage = fundingRepository.findAll(pageable);
        }
        return fundingPage.map(GetFundingResponseDto::from);
    }

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
        log.info("Creating funding: fundingUuid={}, productUuid={}, totalPieces={}", 
                createFundingRequestDto.getFundingUuid(), 
                createFundingRequestDto.getProductUuid(), 
                createFundingRequestDto.getTotalPieces());
        
        try {
            // 1. 게시판 생성 (외부 서비스 호출)
            log.debug("Creating board for funding: {}", createFundingRequestDto.getFundingUuid());
            boardClient.createBoard(new CreateBoardRequestDto(createFundingRequestDto.getFundingUuid()));

            // 2. Redis 설정
            log.debug("Setting Redis data for funding: {}", createFundingRequestDto.getFundingUuid());
            redisService.setRemainingPieces(
                    SetRedisFundingRequestDto.builder()
                            .fundingUuid(createFundingRequestDto.getFundingUuid())
                            .totalPieces(createFundingRequestDto.getTotalPieces())
                            .remainingPieces(createFundingRequestDto.getRemainingPieces())
                            .piecePrice(createFundingRequestDto.getPiecePrice())
                            .build()
            );
            
            // 3. DB 저장
            log.debug("Saving funding to database: {}", createFundingRequestDto.getFundingUuid());
            Funding saved = fundingRepository.save(createFundingRequestDto.toEntity());

            // 4. Outbox 패턴으로 이벤트 저장 (트랜잭션 내에서)
            log.debug("Saving funding event to outbox: {}", saved.getFundingUuid());
            saveFundingEventToOutbox(saved, "FUNDING_CREATED");
            
            log.info("Funding created successfully: fundingUuid={}", saved.getFundingUuid());
        } catch (Exception e) {
            log.error("Failed to create funding: fundingUuid={}, error={}", 
                    createFundingRequestDto.getFundingUuid(), e.getMessage(), e);
            throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR, e);
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

        // 4. Outbox 패턴으로 이벤트 저장 (트랜잭션 내에서)
        log.debug("Saving funding event to outbox: {}", saved.getFundingUuid());
        saveFundingEventToOutbox(saved, "FUNDING_CREATED");

    }

    @Override
    @Transactional
    public void updateFundingStatus(UpdateFundingStatusRequestDto updateFundingStatusRequestDto) {
        Funding entity = fundingRepository.findByFundingUuid(updateFundingStatusRequestDto.getFundingUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_FUNDING));

        FundingStatus current = entity.getFundingStatus();
        FundingStatus target = updateFundingStatusRequestDto.getFundingStatus();
        if ((current == FundingStatus.READY && ( target == FundingStatus.READY || target == FundingStatus.FUNDING)) ||
                (current == FundingStatus.FUNDING && (target == FundingStatus.COMPLETED || target == FundingStatus.CANCELLED))) {
            entity.updateFundingStatus(target);
        } else {
            throw new IllegalStateException("허용되지 않은 상태 변경: " + current + " → " + target);
        }

        //조각 발행
        if(updateFundingStatusRequestDto.getFundingStatus() == FundingStatus.FUNDING){
            createPieces(entity.getProductUuid(), entity.getTotalPieces());
        }

        // 4. Outbox 패턴으로 이벤트 저장 (트랜잭션 내에서)
        log.debug("Saving funding event to outbox: {}", entity.getFundingUuid());
        saveFundingEventToOutbox(entity, "FUNDING_CREATED");

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

    @Override
    public int getRemainingPieces(String fundingUuid) {
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
        Optional<WishFunding> optionalWishFunding =
                wishFundingRepository.getByMemberUuidAndFundingUuid(createWishFundingRequestDto.getMemberUuid(), createWishFundingRequestDto.getFundingUuid());

        if (optionalWishFunding.isPresent()) {
            wishFundingRepository.delete(optionalWishFunding.get());
        } else {
            wishFundingRepository.save(createWishFundingRequestDto.toEntity());
        }
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



    private void saveFundingEventToOutbox(Funding entity, String eventType) {
        try {
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
            
            String payload = objectMapper.writeValueAsString(event);
            outboxService.saveEvent(eventType, "FUNDING", entity.getFundingUuid(), payload);
            
            log.debug("Event saved to outbox: eventType={}, fundingUuid={}", eventType, entity.getFundingUuid());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize funding event: fundingUuid={}, error={}", 
                    entity.getFundingUuid(), e.getMessage(), e);
            throw new RuntimeException("Failed to serialize funding event", e);
        }
    }

    private void deleteFundingEvent(Funding entity){
        saveFundingEventToOutbox(entity, "FUNDING_DELETED");
    }
}
