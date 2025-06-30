package com.pieceofcake.fundingservice.common.exception;


import lombok.Getter;

@Getter
public class FeignClientException extends RuntimeException {
    private final int statusCode;
    private final boolean isSuccess;
    private final int errorCode;
    private final String errorMessage;

    public FeignClientException(int statusCode, boolean isSuccess, int errorCode, String errorMessage) {
        super(errorMessage);
        this.statusCode = statusCode;
        this.isSuccess = isSuccess;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
