package com.pieceofcake.fundingservice.presentation;

import com.pieceofcake.fundingservice.application.FundingService;
import com.pieceofcake.fundingservice.dto.in.FundingCreateRequestDto;
import com.pieceofcake.fundingservice.dto.in.FundingUpdateRequestDto;
import com.pieceofcake.fundingservice.vo.in.FundingCreateRequestVo;
import com.pieceofcake.fundingservice.vo.in.FundingUpdateRequestVo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/funding")
@RequiredArgsConstructor
@RestController
public class FundingController {

    private final FundingService fundingService;

    @Operation(summary = "공모 등록")
    @PostMapping
    public void createFunding(@RequestBody FundingCreateRequestVo fundingCreateRequestVo){
        fundingService.createFunding(FundingCreateRequestDto.from(fundingCreateRequestVo));
    }

    /*
    * todo : 공모 마감일을 등록 시 자동으로 한달 계산하는데 공모 수정 시 마감일도 수정이 가능하게 할지 고정으로 할지 정해야함
    *        공모 중 공모 수정은 안되도록???
    * */
    @Operation(summary = "공모 수정")
    @PutMapping
    public void updateFunding(@RequestBody FundingUpdateRequestVo fundingUpdateRequestVo){
        fundingService.updateFunding(FundingUpdateRequestDto.from(fundingUpdateRequestVo));
    }

    @Operation(summary = "공모 상태 변경")
    @PutMapping("/status")
    public void updateFundingStatus(@RequestBody FundingUpdateRequestVo fundingUpdateRequestVo){
        fundingService.updateFundingStatus(FundingUpdateRequestDto.from(fundingUpdateRequestVo));
    }

    @Operation(summary = "공모 삭제")
    @DeleteMapping("/{fundingUuid}")
    public void deleteFunding(@PathVariable String fundingUuid){
        fundingService.deleteFunding(fundingUuid);
    }
}
