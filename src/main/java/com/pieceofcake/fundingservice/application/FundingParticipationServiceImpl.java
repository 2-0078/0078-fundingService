package com.pieceofcake.fundingservice.application;

import com.pieceofcake.fundingservice.dto.in.ParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.infrastructure.FundingParticipationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundingParticipationServiceImpl implements FundingParticipationService {

    private final FundingParticipationRepository participationRepository;

    @Override
    public void joinFunding(ParticipateFundingRequestDto fundingJoinRequestDto) {
        participationRepository.save(fundingJoinRequestDto.toEntity());
    }

    @Override
    public void leaveFunding() {

    }

    @Override
    public void getMyFunding() {

    }
}
