package com.pieceofcake.fundingservice.application;

import com.pieceofcake.fundingservice.dto.in.ParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.dto.out.GetParticipateFundingResponseDto;
import com.pieceofcake.fundingservice.entity.FundingParticipation;
import com.pieceofcake.fundingservice.entity.ParticipateStatus;
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
    public void getMyFundings(String memberUuid) {

    }

    @Override
    public int getMyTotalParticipationQuantity(String fundingUuid, String memberUuid) {
        return participationRepository.findByFundingUuidAndMemberUuidAndParticipateStatus(fundingUuid, memberUuid,ParticipateStatus.JOIN)
                .stream()
                .mapToInt(FundingParticipation::getQuantity)
                .sum();
    }

    @Override
    public void cancelParticipation(String fundingUuid, String memberUuid) {
        participationRepository.cancelParticipation(fundingUuid,memberUuid, ParticipateStatus.CANCEL);
    }
}