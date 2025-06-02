package com.pieceofcake.fundingservice.application;

import com.pieceofcake.fundingservice.common.entity.BaseResponseStatus;
import com.pieceofcake.fundingservice.common.exception.BaseException;
import com.pieceofcake.fundingservice.dto.in.ParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.infrastructure.FundingParticipationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundingParticipationServiceImpl implements FundingParticipationService {

//    private final RedisService redisService;

    private final FundingParticipationRepository participationRepository;

    @Transactional
    @Override
    public void joinFunding(ParticipateFundingRequestDto fundingJoinRequestDto) {

    }

    @Override
    public void leaveFunding() {

    }

    @Override
    public void getRemainingPieces() {

    }

    @Override
    public void getMyFunding() {

    }
}
