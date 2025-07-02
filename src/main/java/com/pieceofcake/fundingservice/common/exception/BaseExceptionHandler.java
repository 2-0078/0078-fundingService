package com.pieceofcake.fundingservice.common.exception;


import com.pieceofcake.fundingservice.common.entity.BaseResponseEntity;
import com.pieceofcake.fundingservice.common.entity.BaseResponseStatus;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
@Slf4j
public class BaseExceptionHandler {

    /**
     * 발생한 예외 처리
     */

    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<BaseResponseEntity<Void>> BaseError(BaseException e) {
        BaseResponseEntity<Void> response = new BaseResponseEntity<>(e.getStatus());
        log.error("BaseException -> {}({})", e.getStatus(), e.getStatus().getMessage(), e);
        return new ResponseEntity<>(response, response.httpStatus());
    }


    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<BaseResponseEntity<Void>> RuntimeError(RuntimeException e) {
        BaseResponseEntity<Void> response = new BaseResponseEntity<>(BaseResponseStatus.INTERNAL_SERVER_ERROR);
        log.error("RuntimeException occurred: {}", e.getMessage(), e);
        return new ResponseEntity<>(response, response.httpStatus());
    }

    @ExceptionHandler(FeignClientException.class)
    protected ResponseEntity<BaseResponseEntity<Void>> handleFeignClientException(FeignClientException e) {
        // 원격 서비스의 응답 정보를 그대로 클라이언트에게 반환
        BaseResponseEntity<Void> response = new BaseResponseEntity<>(
                org.springframework.http.HttpStatus.valueOf(e.getStatusCode()),
                e.isSuccess(),
                e.getErrorMessage(),
                e.getErrorCode(),
                null
        );

        log.error("FeignClientException -> code: {}, message: {}", e.getErrorCode(), e.getErrorMessage(), e);
        return ResponseEntity.status(e.getStatusCode()).body(response);
    }
}