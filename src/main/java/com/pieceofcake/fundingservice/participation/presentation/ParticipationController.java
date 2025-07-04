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


    @Operation(
        summary = "공모 참여",
        description = "사용자가 특정 공모에 참여합니다.\n\n입력값: 참여 정보(참여 수량 등)\n\n참여 상태값: [JOIN, CANCEL]",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "X-Member-Uuid", description = "회원 UUID", in = io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER, required = true)
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "참여 정보",
            required = true
        ),
        tags = {"Participation"}
    )
    @PostMapping
    public BaseResponseEntity<Void> participateFunding(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @RequestBody ParticipateFundingRequestVo fundingJoinRequestVo
    ){
        participationService.participateFunding(ParticipateFundingRequestDto.from(fundingJoinRequestVo, memberUuid));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(
        summary = "공모 참여 취소",
        description = "사용자가 특정 공모 참여를 취소합니다.\n\n입력값: fundingUuid (path)\n\n참여 상태값: [JOIN, CANCEL]",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "X-Member-Uuid", description = "회원 UUID", in = io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER, required = true),
            @io.swagger.v3.oas.annotations.Parameter(name = "fundingUuid", description = "공모 UUID", in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH, required = true)
        },
        tags = {"Participation"}
    )
    @DeleteMapping("/{fundingUuid}")
    public BaseResponseEntity<Void> cancelFunding(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @PathVariable String fundingUuid
    ){
        participationService.cancelParticipation(
                ParticipateFundingRequestDto.builder()
                        .fundingUuid(fundingUuid)
                        .memberUuid(memberUuid)
                        .participateStatus(ParticipateStatus.CANCEL)
                        .build()
        );
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(
        summary = "공모 참여 여부 조회",
        description = "사용자가 특정 공모에 참여했는지 여부를 조회합니다.\n\n입력값: fundingUuid (path)",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "X-Member-Uuid", description = "회원 UUID", in = io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER, required = true),
            @io.swagger.v3.oas.annotations.Parameter(name = "fundingUuid", description = "공모 UUID", in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH, required = true)
        },
        tags = {"Participation"}
    )
    @GetMapping("/{fundingUuid}")
    public BaseResponseEntity<Boolean> getParticipateFunding(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @PathVariable String fundingUuid
    ){
        return new BaseResponseEntity<>(participationService.getMyFunding(
                ParticipateFundingRequestDto.builder()
                .fundingUuid(fundingUuid)
                .memberUuid(memberUuid)
                .build()));
    }

    @Operation(
        summary = "공모 상품에서 구매한 조각 총합 조회",
        description = "사용자가 해당 공모 상품에서 구매한 조각의 총합을 조회합니다.\n\n입력값: fundingUuid (path)",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "X-Member-Uuid", description = "회원 UUID", in = io.swagger.v3.oas.annotations.enums.ParameterIn.HEADER, required = true),
            @io.swagger.v3.oas.annotations.Parameter(name = "fundingUuid", description = "공모 UUID", in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH, required = true)
        },
        tags = {"Participation"}
    )
    @GetMapping("/total/{fundingUuid}")
    public BaseResponseEntity<Integer> getParticipateFundingTotal(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @PathVariable String fundingUuid
    ){
        return new BaseResponseEntity<>(participationService.getMyTotalParticipationQuantity(
                ParticipateFundingRequestDto.builder()
                        .fundingUuid(fundingUuid)
                        .memberUuid(memberUuid)
                        .build()));
    }

    @Operation(
        summary = "남은 조각 수 조회",
        description = "해당 공모 상품의 남은 조각 수를 조회합니다.\n\n입력값: fundingUuid (path)",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(name = "fundingUuid", description = "공모 UUID", in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH, required = true)
        },
        tags = {"Participation"}
    )
    @GetMapping("/remain/{fundingUuid}")
    public BaseResponseEntity<Integer> getRemainPieces(@PathVariable String fundingUuid){
        return new BaseResponseEntity<>(participationService.getRemainingPieces(fundingUuid));
    }
}
