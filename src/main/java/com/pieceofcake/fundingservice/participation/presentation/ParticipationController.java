package com.pieceofcake.fundingservice.participation.presentation;

import com.pieceofcake.fundingservice.common.entity.BaseResponseEntity;
import com.pieceofcake.fundingservice.common.entity.BaseResponseStatus;
import com.pieceofcake.fundingservice.participation.application.FundingParticipationService;
import com.pieceofcake.fundingservice.participation.dto.in.ParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.participation.entity.ParticipateStatus;
import com.pieceofcake.fundingservice.participation.vo.in.ParticipateFundingRequestVo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/participation")
@RequiredArgsConstructor
@RestController
public class ParticipationController {

    private final FundingParticipationService participationService;


    @Operation(summary = "(사용자)공모 참여")
    @PostMapping
    public BaseResponseEntity<Void> participateFunding(@RequestBody ParticipateFundingRequestVo fundingJoinRequestVo){
        String memberUuid = "member1212";
        participationService.participateFunding(ParticipateFundingRequestDto.from(fundingJoinRequestVo, memberUuid));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(summary = "(사용자)공모 취소")
    @DeleteMapping("/{fundingUuid}")
    public BaseResponseEntity<Void> cancelFunding(@PathVariable String fundingUuid){
        String memberUuid = "member1212";
//        participationService.cancelParticipation(fundingUuid, memberUuid);
        participationService.cancelParticipation(
                ParticipateFundingRequestDto.builder()
                        .fundingUuid(fundingUuid)
                        .memberUuid(memberUuid)
                        .participateStatus(ParticipateStatus.CANCEL)
                        .build()
        );
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(summary = "공모 참여 여부 조회")
    @GetMapping("/{fundingUuid}")
    public BaseResponseEntity<Boolean> getParticipateFunding(@PathVariable String fundingUuid){
        String memberUuid = "member1212";
        return new BaseResponseEntity<>(participationService.getMyFunding(
                ParticipateFundingRequestDto.builder()
                .fundingUuid(fundingUuid)
                .memberUuid(memberUuid)
                .build()));
    }

    @Operation(summary = "해당 공모 상품에서 구매한 조각 총합 조회")
    @GetMapping("/total/{fundingUuid}")
    public BaseResponseEntity<Integer> getParticipateFundingTotal(@PathVariable String fundingUuid){
        String memberUuid = "member1212";
        return new BaseResponseEntity<>(participationService.getMyTotalParticipationQuantity(
                ParticipateFundingRequestDto.builder()
                        .fundingUuid(fundingUuid)
                        .memberUuid(memberUuid)
                        .build()));
    }

    @Operation(summary = "남은 조각 수 조회")
    @GetMapping("/remain/{fundingUuid}")
    public BaseResponseEntity<Integer> getRemainPieces(@PathVariable String fundingUuid){
        String memberUuid = "member1212";
        System.out.println("sasss");
        return new BaseResponseEntity<>(participationService.getRemainingPieces(fundingUuid));
    }
}
