package com.pieceofcake.fundingservice.presentation;

import com.pieceofcake.fundingservice.application.FundingService;
import com.pieceofcake.fundingservice.common.entity.BaseResponseEntity;
import com.pieceofcake.fundingservice.common.entity.BaseResponseStatus;
import com.pieceofcake.fundingservice.dto.in.FundingCreateRequestDto;
import com.pieceofcake.fundingservice.dto.in.FundingUpdateRequestDto;
import com.pieceofcake.fundingservice.dto.out.FundingResponseDto;
import com.pieceofcake.fundingservice.vo.in.FundingCreateRequestVo;
import com.pieceofcake.fundingservice.vo.in.FundingUpdateRequestVo;
import com.pieceofcake.fundingservice.vo.out.FundingResponseVo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/funding")
@RequiredArgsConstructor
@RestController
public class FundingController {

    private final FundingService fundingService;

    @Operation(summary = "공모 리스트 조회")
    @GetMapping
    public BaseResponseEntity<List<FundingResponseVo>> getFundingList(){
        return new BaseResponseEntity<>(fundingService.getFundingList().stream().map(FundingResponseDto::toVo).toList());
    }

    @Operation(summary = "공모 UUID 리스트 조회")
    @GetMapping("/list")
    public BaseResponseEntity<List<String>> getFundingUuidList(){
        return new BaseResponseEntity<>(fundingService.getFundingUuidList());
    }

    @Operation(summary = "공모 상세 조회")
    @GetMapping("/{fundingUuid}")
    public BaseResponseEntity<FundingResponseVo> getFunding(@PathVariable String fundingUuid){
        return new BaseResponseEntity<>(fundingService.getFunding(fundingUuid).toVo());
    }

    @Operation(summary = "공모 등록")
    @PostMapping
    public BaseResponseEntity<Void> createFunding(@RequestBody FundingCreateRequestVo fundingCreateRequestVo){
        fundingService.createFunding(FundingCreateRequestDto.from(fundingCreateRequestVo));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    /*
    * todo : 공모 마감일을 등록 시 자동으로 한달 계산하는데 공모 수정 시 마감일도 수정이 가능하게 할지 고정으로 할지 정해야함
    * */
    @Operation(summary = "공모 수정")
    @PutMapping
    public BaseResponseEntity<Void> updateFunding(@RequestBody FundingUpdateRequestVo fundingUpdateRequestVo){
        fundingService.updateFunding(FundingUpdateRequestDto.from(fundingUpdateRequestVo));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(summary = "공모 상태 변경")
    @PutMapping("/status")
    public BaseResponseEntity<Void> updateFundingStatus(@RequestBody FundingUpdateRequestVo fundingUpdateRequestVo){
        fundingService.updateFundingStatus(FundingUpdateRequestDto.from(fundingUpdateRequestVo));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(summary = "공모 삭제")
    @DeleteMapping("/{fundingUuid}")
    public BaseResponseEntity<Void> deleteFunding(@PathVariable String fundingUuid){
        fundingService.deleteFunding(fundingUuid);
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }
}
