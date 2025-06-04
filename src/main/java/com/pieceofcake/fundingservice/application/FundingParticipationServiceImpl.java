package com.pieceofcake.fundingservice.application;

import com.pieceofcake.fundingservice.common.entity.BaseResponseStatus;
import com.pieceofcake.fundingservice.common.exception.BaseException;
import com.pieceofcake.fundingservice.dto.in.ParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.dto.out.GetParticipateFundingResponseDto;
import com.pieceofcake.fundingservice.entity.ParticipateStatus;
import com.pieceofcake.fundingservice.infrastructure.FundingParticipationRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<GetParticipateFundingResponseDto> getMyFundingParticipations(String fundingUuid, String memberUuid) {
        return participationRepository.findByFundingUuidAndMemberUuid(fundingUuid,memberUuid)
                .stream().map(GetParticipateFundingResponseDto::from).toList();
    }

    @Override
    public void getMyFundings(String memberUuid) {

    }

    @Override
    public int getMyTotalParticipationQuantity(String fundingUuid, String memberUuid) {
        return participationRepository.findMyTotalParticipationQuantity(fundingUuid, memberUuid)
                .orElseThrow(()-> new BaseException(BaseResponseStatus.CANNOT_CANCEL_PARTICIPATION));
    }

    @Override
    public void cancelParticipation(String fundingUuid, String memberUuid) {
        participationRepository.cancelParticipation(fundingUuid,memberUuid, ParticipateStatus.CANCEL);
    }
}