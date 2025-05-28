package com.pieceofcake.fundingservice.application;

import com.pieceofcake.fundingservice.dto.in.FundingCreateRequestDto;
import com.pieceofcake.fundingservice.dto.in.FundingUpdateRequestDto;
import com.pieceofcake.fundingservice.entity.Funding;
import com.pieceofcake.fundingservice.entity.FundingStatus;
import com.pieceofcake.fundingservice.infrastructure.FundingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class FundingServiceImpl implements FundingService {

    private final FundingRepository fundingRepository;

    @Override
    public void createFunding(FundingCreateRequestDto fundingCreateRequestDto) {
        fundingRepository.save(fundingCreateRequestDto.toEntity());
    }

    @Override
    public void updateFunding(FundingUpdateRequestDto fundingUpdateRequestDto) {
        Funding entity = fundingRepository.findByFundingUuid(fundingUpdateRequestDto.getFundingUuid())
                .orElseThrow(() -> new IllegalArgumentException("no funding"));

        if(entity.getFundingStatus() != FundingStatus.READY){
            throw new IllegalArgumentException("funding status is not ready");
        }
        fundingRepository.save(fundingUpdateRequestDto.toEntity(entity));
    }

    @Override
    @Transactional
    public void updateFundingStatus(FundingUpdateRequestDto fundingUpdateRequestDto) {
        Funding entity = fundingRepository.findByFundingUuid(fundingUpdateRequestDto.getFundingUuid())
                .orElseThrow(() -> new IllegalArgumentException("no funding"));

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
                .orElseThrow(() -> new IllegalArgumentException("no funding"));

        if(entity.getFundingStatus() == FundingStatus.FUNDING){
            throw new IllegalArgumentException("funding status is funding");
        }
        entity.deleteFunding();
    }
}
