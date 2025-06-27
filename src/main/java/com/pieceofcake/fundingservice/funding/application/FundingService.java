package com.pieceofcake.fundingservice.funding.application;

import com.pieceofcake.fundingservice.funding.dto.in.*;
import com.pieceofcake.fundingservice.funding.dto.out.GetFundingResponseDto;
import com.pieceofcake.fundingservice.funding.dto.out.GetWishFundingResponseDto;
import com.pieceofcake.fundingservice.funding.entity.FundingStatus;
import com.pieceofcake.fundingservice.participation.dto.in.CancelParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.participation.dto.in.ParticipateFundingRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FundingService {
    //admin
    List<GetFundingResponseDto> getFundingList(FundingStatus status);
    Page<GetFundingResponseDto> getFundingListWithPaging(FundingStatus status, Pageable pageable);
    List<String> getFundingUuidList();
    GetFundingResponseDto getFunding(String fundingUuid);
    void createFunding(CreateFundingRequestDto createFundingRequestDto);
    void updateFunding(UpdateFundingRequestDto updateFundingRequestDto);
    void updateFundingStatus(UpdateFundingRequestDto updateFundingRequestDto);
    void deleteFunding(String fundingUuid);
//    void participateFunding(ParticipateFundingRequestDto fundingJoinRequestDto);
//    void cancelFunding(CancelParticipateFundingRequestDto cancelDto);
    int getRemainingPieces(String fundingUuid);
    List<GetWishFundingResponseDto> getWishFundingList(String memberUuid);
    Boolean isWishFunding(String fundingUuid, String memberUuid);
    void wishFunding(CreateWishFundingRequestDto createWishFundingRequestDto);
    void cancelWishFunding(Long id);
}
