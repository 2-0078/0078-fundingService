package com.pieceofcake.fundingservice.application;

import com.pieceofcake.fundingservice.common.entity.BaseResponseStatus;
import com.pieceofcake.fundingservice.common.exception.BaseException;
import com.pieceofcake.fundingservice.dto.in.FundingCreateRequestDto;
import com.pieceofcake.fundingservice.dto.in.FundingUpdateRequestDto;
import com.pieceofcake.fundingservice.dto.out.FundingResponseDto;
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

    /*
    * todo : findAll -> 페이징이나 다른걸로 바꿔야함
    * */
    @Override
    public List<FundingResponseDto> getFundingList() {
        return fundingRepository.findAll().stream().map(FundingResponseDto::from).toList();
    }

    @Override
    public List<String> getFundingUuidList() {
        return fundingRepository.findAll().stream().map(Funding::getFundingUuid).toList();
    }

    @Override
    public FundingResponseDto getFunding(String fundingUuid) {
        return FundingResponseDto.from(fundingRepository.findByFundingUuid(fundingUuid)
                .orElseThrow(()-> new IllegalArgumentException("no funding")) );
    }

    @Override
    public void createFunding(FundingCreateRequestDto fundingCreateRequestDto) {
        fundingRepository.save(fundingCreateRequestDto.toEntity());
    }

    @Override
    public void updateFunding(FundingUpdateRequestDto fundingUpdateRequestDto) {
        Funding entity = fundingRepository.findByFundingUuid(fundingUpdateRequestDto.getFundingUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_FUNDING));

        if(entity.getFundingStatus() != FundingStatus.READY){
            throw new BaseException(BaseResponseStatus.FAILED_TO_UPDATE);
        }
        fundingRepository.save(fundingUpdateRequestDto.toEntity(entity));
    }

    @Override
    @Transactional
    public void updateFundingStatus(FundingUpdateRequestDto fundingUpdateRequestDto) {
        Funding entity = fundingRepository.findByFundingUuid(fundingUpdateRequestDto.getFundingUuid())
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NO_EXIST_FUNDING));

        FundingStatus current = entity.getFundingStatus();
        FundingStatus target = fundingUpdateRequestDto.getFundingStatus();
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
}
