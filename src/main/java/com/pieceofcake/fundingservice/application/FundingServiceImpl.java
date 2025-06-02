package com.pieceofcake.fundingservice.application;

import com.pieceofcake.fundingservice.common.entity.BaseResponseStatus;
import com.pieceofcake.fundingservice.common.exception.BaseException;
import com.pieceofcake.fundingservice.dto.in.CreateFundingRequestDto;
import com.pieceofcake.fundingservice.dto.in.ParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.dto.in.UpdateFundingRequestDto;
import com.pieceofcake.fundingservice.dto.out.GetFundingResponseDto;
import com.pieceofcake.fundingservice.entity.Funding;
import com.pieceofcake.fundingservice.entity.FundingStatus;
import com.pieceofcake.fundingservice.infrastructure.FundingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class FundingServiceImpl implements FundingService {

    private final FundingRepository fundingRepository;
    private final FundingParticipationService participationService;

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

    @Override
    public void createFunding(CreateFundingRequestDto createFundingRequestDto) {
        fundingRepository.save(createFundingRequestDto.toEntity());
    }

    @Override
    public void updateFunding(UpdateFundingRequestDto updateFundingRequestDto) {
        Funding entity = fundingRepository.findByFundingUuid(updateFundingRequestDto.getFundingUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_FUNDING));

        if(entity.getFundingStatus() != FundingStatus.READY){
            throw new BaseException(BaseResponseStatus.FAILED_TO_UPDATE);
        }
        fundingRepository.save(updateFundingRequestDto.toEntity(entity));
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
    }

    @Transactional
    @Override
    public void participateFunding(ParticipateFundingRequestDto fundingJoinRequestDto) {
        //남은 수량 조회, 0이면 throw 조각 없음 오류
        //남은 수량 - 주문수량(비관적락), 계산 결과가 0보다 작으면 남은 수량이 0이 될때까지의 값만 처리(일부만 조각 결제)
        //참여 내역 저장


        //수량 조회
        if(getRemainingPieces(fundingJoinRequestDto.getFundingUuid()) == 0){
            throw new BaseException(BaseResponseStatus.NO_MORE_PIECES);
        }
        //수량 차감


    }

    @Override
    public int getRemainingPieces(String fundingUuid) {
        return fundingRepository.findByFundingUuid(fundingUuid).orElseThrow(
                ()-> new BaseException(BaseResponseStatus.NO_EXIST_FUNDING)
        ).getRemainingPieces();
    }

}
