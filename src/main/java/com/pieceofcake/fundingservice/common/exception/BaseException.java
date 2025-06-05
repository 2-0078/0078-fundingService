package com.pieceofcake.fundingservice.common.exception;

import com.pieceofcake.fundingservice.common.entity.BaseResponseStatus;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{

    private final BaseResponseStatus status;

    public BaseException(BaseResponseStatus status) {
        super(status.getMessage());
        this.status = status;
    }

    public BaseException(BaseResponseStatus status, Throwable cause) {
        super(status.getMessage(), cause);
        this.status = status;
    }

    public BaseResponseStatus getStatus() {
        return status;
    }
}
