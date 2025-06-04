package com.pieceofcake.fundingservice.presentation;

import com.pieceofcake.fundingservice.application.FundingParticipationService;
import com.pieceofcake.fundingservice.application.FundingService;
import com.pieceofcake.fundingservice.common.entity.BaseResponseEntity;
import com.pieceofcake.fundingservice.common.entity.BaseResponseStatus;
import com.pieceofcake.fundingservice.dto.in.CancelParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.dto.in.CreateFundingRequestDto;
import com.pieceofcake.fundingservice.dto.in.ParticipateFundingRequestDto;
import com.pieceofcake.fundingservice.dto.in.UpdateFundingRequestDto;
import com.pieceofcake.fundingservice.vo.in.CreateFundingRequestVo;
import com.pieceofcake.fundingservice.vo.in.ParticipateFundingRequestVo;
import com.pieceofcake.fundingservice.vo.in.UpdateFundingRequestVo;
import com.pieceofcake.fundingservice.vo.out.GetFundingResponseVo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/funding")
@RequiredArgsConstructor
@RestController
public class FundingController {

    private final FundingService fundingService;
    private final FundingParticipationService fundingParticipationService;

    @Operation(summary = "공모 리스트 조회")
    @GetMapping
    public BaseResponseEntity<List<GetFundingResponseVo>> getFundingList(){
//        return new BaseResponseEntity<>(fundingService.getFundingList().stream().map(GetFundingResponseDto::toVo).toList());
        return null;
    }

    @Operation(summary = "공모 UUID 리스트 조회")
    @GetMapping("/list")
    public BaseResponseEntity<List<String>> getFundingUuidList(){
        return new BaseResponseEntity<>(fundingService.getFundingUuidList());
    }

    @Operation(summary = "공모 상세 조회")
    @GetMapping("/{fundingUuid}")
    public BaseResponseEntity<GetFundingResponseVo> getFunding(@PathVariable String fundingUuid){
        return new BaseResponseEntity<>(fundingService.getFunding(fundingUuid).toVo());
    }

    @Operation(summary = "공모 등록")
    @PostMapping
    public BaseResponseEntity<Void> createFunding(@RequestBody CreateFundingRequestVo createFundingRequestVo){
        fundingService.createFunding(CreateFundingRequestDto.from(createFundingRequestVo));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    /*
    * todo : 공모 마감일을 등록 시 자동으로 한달 계산하는데 공모 수정 시 마감일도 수정이 가능하게 할지 고정으로 할지 정해야함
    * */
    @Operation(summary = "공모 수정")
    @PutMapping
    public BaseResponseEntity<Void> updateFunding(@RequestBody UpdateFundingRequestVo updateFundingRequestVo){
        fundingService.updateFunding(UpdateFundingRequestDto.from(updateFundingRequestVo));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(summary = "공모 상태 변경")
    @PutMapping("/status")
    public BaseResponseEntity<Void> updateFundingStatus(@RequestBody UpdateFundingRequestVo updateFundingRequestVo){
        fundingService.updateFundingStatus(UpdateFundingRequestDto.from(updateFundingRequestVo));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(summary = "공모 삭제")
    @DeleteMapping("/{fundingUuid}")
    public BaseResponseEntity<Void> deleteFunding(@PathVariable String fundingUuid){
        fundingService.deleteFunding(fundingUuid);
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(summary = "(사용자)공모 참여")
    @PostMapping("/participation")
    public BaseResponseEntity<Void> participateFunding(@RequestBody ParticipateFundingRequestVo fundingJoinRequestVo){
        fundingService.participateFunding(ParticipateFundingRequestDto.from(fundingJoinRequestVo));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(summary = "(사용자)공모 취소")
    @DeleteMapping("/participation/{fundingUuid}")
    public BaseResponseEntity<Void> cancelFunding(@PathVariable String fundingUuid){
        String memberUuid = "member1212";
        fundingService.cancelFunding(CancelParticipateFundingRequestDto.from(fundingUuid,memberUuid));
        return new BaseResponseEntity<>(BaseResponseStatus.SUCCESS);
    }

    @Operation(summary = "공모 잔여 조각 조회")
    @GetMapping("/remain/{fundingUuid}")
    public BaseResponseEntity<Integer> getFundingRemainPieces(@PathVariable String fundingUuid){
        return new BaseResponseEntity<>(fundingService.getRemainingPieces(fundingUuid));
    }
}
