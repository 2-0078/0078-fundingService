package com.pieceofcake.fundingservice.funding.presentation;

import com.pieceofcake.fundingservice.funding.application.FundingService;
import com.pieceofcake.fundingservice.common.entity.BaseResponseEntity;
import com.pieceofcake.fundingservice.common.entity.BaseResponseStatus;
import com.pieceofcake.fundingservice.funding.dto.in.*;
import com.pieceofcake.fundingservice.funding.dto.out.GetFundingResponseDto;
import com.pieceofcake.fundingservice.funding.dto.out.GetWishFundingResponseDto;
import com.pieceofcake.fundingservice.funding.entity.FundingStatus;
import com.pieceofcake.fundingservice.funding.vo.in.CreateFundingRequestVo;
import com.pieceofcake.fundingservice.funding.vo.in.CreateWishFundingRequestVo;
import com.pieceofcake.fundingservice.funding.vo.in.UpdateFundingRequestVo;
import com.pieceofcake.fundingservice.funding.vo.in.UpdateFundingStatusRequestVo;
import com.pieceofcake.fundingservice.funding.vo.out.GetFundingResponseVo;
import com.pieceofcake.fundingservice.funding.vo.out.GetWishFundingResponseVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/funding")
@RequiredArgsConstructor
@RestController
public class FundingController {

    private final FundingService fundingService;

    @Operation(
        summary = "공모 리스트 조회 (상태별) - 더미데이터용! 실사용 X",
        description = "특정 상태의 공모 리스트를 페이지네이션하여 조회합니다.\n\nstatus 값은 다음과 같습니다:\n- READY: 준비중\n- FUNDING: 펀딩 진행중\n- COMPLETED: 펀딩 성공\n- CANCELLED: 펀딩 취소\n\nstatus는 [READY, FUNDING, COMPLETED, CANCELLED] 중 하나입니다.",
        parameters = {
            @Parameter(
                name = "status",
                description = "공모 상태 (READY: 준비중, FUNDING: 펀딩 진행중, COMPLETED: 펀딩 성공, CANCELLED: 펀딩 취소)",
                in = ParameterIn.PATH,
                required = false,
                schema = @Schema(allowableValues = {"READY", "FUNDING", "COMPLETED", "CANCELLED"})
            ),
            @Parameter(name = "page", description = "페이지 번호", in = ParameterIn.QUERY, required = false, example = "0"),
            @Parameter(name = "size", description = "페이지 크기", in = ParameterIn.QUERY, required = false, example = "30")
        },
        tags = {"Funding"}
    )
    @GetMapping("/all/{status}")
    public BaseResponseEntity<Page<GetFundingResponseVo>> getFundingList(
            @PathVariable(required = false) FundingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GetFundingResponseDto> fundingPage = fundingService.getFundingListWithPaging(status, pageable);
        Page<GetFundingResponseVo> responsePage = fundingPage.map(GetFundingResponseDto::toVo);
        return new BaseResponseEntity<>(responsePage);
    }

    @Operation(
        summary = "전체 공모 리스트 조회  - 더미데이터용! 실사용 X",
        description = "모든 공모 리스트를 페이지네이션하여 조회합니다.",
        parameters = {
            @Parameter(name = "page", description = "페이지 번호", in = ParameterIn.QUERY, required = false, example = "0"),
            @Parameter(name = "size", description = "페이지 크기", in = ParameterIn.QUERY, required = false, example = "30")
        },
        tags = {"Funding"}
    )
    @GetMapping("/all")
    public BaseResponseEntity<Page<GetFundingResponseVo>> getAllFundingList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GetFundingResponseDto> fundingPage = fundingService.getFundingListWithPaging(null, pageable);
        Page<GetFundingResponseVo> responsePage = fundingPage.map(GetFundingResponseDto::toVo);
        return new BaseResponseEntity<>(responsePage);
    }

    @Operation(
        summary = "공모 UUID 리스트 조회 - 더미데이터용! 실사용 X",
        description = "등록된 모든 공모의 UUID 리스트를 반환합니다.",
        tags = {"Funding"}
    )
    @GetMapping("/list")
    public BaseResponseEntity<List<String>> getFundingUuidList(){
        return new BaseResponseEntity<>(fundingService.getFundingUuidList());
    }

    @Operation(
        summary = "공모 상세 조회 - 더미데이터용! 실사용 X",
        description = "특정 공모 UUID에 해당하는 공모의 상세 정보를 반환합니다.",
        parameters = {
            @Parameter(name = "fundingUuid", description = "공모 UUID", in = ParameterIn.PATH, required = true)
        },
        tags = {"Funding"}
    )
    @GetMapping("/{fundingUuid}")
    public BaseResponseEntity<GetFundingResponseVo> getFunding(@PathVariable String fundingUuid){
        return new BaseResponseEntity<>(fundingService.getFunding(fundingUuid).toVo());
    }

    @Operation(
        summary = "공모 등록",
        description = "새로운 공모를 등록합니다.",
        parameters = {
            @Parameter(name = "X-Member-Uuid", description = "회원 UUID", in = ParameterIn.HEADER, required = true)
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "공모 등록 정보 ",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateFundingRequestVo.class))
        ),
        tags = {"Funding"}
    )
    @PostMapping
    public BaseResponseEntity<Void> createFunding(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @RequestBody CreateFundingRequestVo createFundingRequestVo){
        fundingService.createFunding(CreateFundingRequestDto.from(createFundingRequestVo));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(
        summary = "공모 수정",
        description = "기존 공모의 정보를 수정합니다.\n\n상태 전이: READY -> READY/FUNDING, FUNDING -> COMPLETED/CANCELLED",
        parameters = {
            @Parameter(name = "X-Member-Uuid", description = "회원 UUID", in = ParameterIn.HEADER, required = true)
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "수정할 공모 정보 ",
            required = true,
            content = @Content(schema = @Schema(implementation = UpdateFundingRequestVo.class))
        ),
        tags = {"Funding"}
    )
    @PutMapping
    public BaseResponseEntity<Void> updateFunding(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @RequestBody UpdateFundingRequestVo updateFundingRequestVo){
        fundingService.updateFunding(UpdateFundingRequestDto.from(updateFundingRequestVo));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(
        summary = "공모 상태 변경",
        description = "공모의 상태(READY, FUNDING, COMPLETED, CANCELLED) 중 하나로 변경합니다.\n\n상태 전이: READY -> READY/FUNDING, FUNDING -> COMPLETED/CANCELLED",
        parameters = {
            @Parameter(name = "X-Member-Uuid", description = "회원 UUID", in = ParameterIn.HEADER, required = true)
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "상태 변경 정보 (UpdateFundingStatusRequestVo)",
            required = true,
            content = @Content(schema = @Schema(implementation = UpdateFundingStatusRequestVo.class))
        ),
        tags = {"Funding"}
    )
    @PutMapping("/status")
    public BaseResponseEntity<Void> updateFundingStatus(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @RequestBody UpdateFundingStatusRequestVo updateFundingStatusRequestVo){
        fundingService.updateFundingStatus(UpdateFundingStatusRequestDto.from(updateFundingStatusRequestVo));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(
        summary = "공모 삭제",
        description = "특정 공모 UUID에 해당하는 공모를 삭제합니다.",
        parameters = {
            @Parameter(name = "X-Member-Uuid", description = "회원 UUID", in = ParameterIn.HEADER, required = true),
            @Parameter(name = "fundingUuid", description = "삭제할 공모 UUID", in = ParameterIn.PATH, required = true)
        },
        tags = {"Funding"}
    )
    @DeleteMapping("/{fundingUuid}")
    public BaseResponseEntity<Void> deleteFunding(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @PathVariable String fundingUuid){
        fundingService.deleteFunding(fundingUuid);
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(
        summary = "찜한 공모 전체 조회",
        description = "회원이 찜한 모든 공모를 조회합니다.",
        parameters = {
            @Parameter(name = "X-Member-Uuid", description = "회원 UUID", in = ParameterIn.HEADER, required = true)
        },
        tags = {"Funding"}
    )
    @GetMapping("/wish")
    public BaseResponseEntity<List<GetWishFundingResponseVo>> getWishFunding(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid
    ){
        return new BaseResponseEntity<>(fundingService.getWishFundingList(memberUuid).stream().map(GetWishFundingResponseDto::toVo).toList());
    }

    @Operation(
        summary = "공모 상품 찜 여부 조회",
        description = "특정 공모 상품을 회원이 찜했는지 여부를 조회합니다.\n\n fundingUuid (path)",
        parameters = {
            @Parameter(name = "X-Member-Uuid", description = "회원 UUID", in = ParameterIn.HEADER, required = true),
            @Parameter(name = "fundingUuid", description = "공모 UUID", in = ParameterIn.PATH, required = true)
        },
        tags = {"Funding"}
    )
    @GetMapping("/wish/{fundingUuid}")
    public BaseResponseEntity<Boolean> isWishFunding(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @PathVariable String fundingUuid){
        return new BaseResponseEntity<>(fundingService.isWishFunding(fundingUuid, memberUuid));
    }

    @Operation(
        summary = "공모 찜하기",
        description = "특정 공모 상품을 회원이 찜합니다.",
        parameters = {
            @Parameter(name = "X-Member-Uuid", description = "회원 UUID", in = ParameterIn.HEADER, required = true)
        },
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "찜할 공모 정보",
            required = true,
            content = @Content(schema = @Schema(implementation = CreateWishFundingRequestVo.class))
        ),
        tags = {"Funding"}
    )
    @PostMapping("/wish")
    public BaseResponseEntity<Void> wishFunding(
            @RequestHeader(value = "X-Member-Uuid") String memberUuid,
            @RequestBody CreateWishFundingRequestVo createWishFundingRequestVo){
        fundingService.wishFunding(CreateWishFundingRequestDto.from(createWishFundingRequestVo, memberUuid));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }
}
