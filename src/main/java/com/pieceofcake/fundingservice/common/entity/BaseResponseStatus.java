package com.pieceofcake.fundingservice.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;


@Getter
@AllArgsConstructor
public enum BaseResponseStatus {

    /**
     * 200: 요청 성공
     **/
    SUCCESS(HttpStatus.OK, true, 200, "요청에 성공하였습니다."),

    /**
     * 400 : security 에러
     */
    WRONG_JWT_TOKEN(HttpStatus.UNAUTHORIZED, false, 401, "다시 로그인 해주세요"),
    NO_SIGN_IN(HttpStatus.UNAUTHORIZED, false, 402, "로그인을 먼저 진행해주세요"),
    NO_ACCESS_AUTHORITY(HttpStatus.FORBIDDEN, false, 403, "접근 권한이 없습니다"),
    DISABLED_USER(HttpStatus.FORBIDDEN, false, 404, "비활성화된 계정입니다. 계정을 복구하시겠습니까?"),
    FAILED_TO_RESTORE(HttpStatus.INTERNAL_SERVER_ERROR, false, 405, "계정 복구에 실패했습니다. 관리자에게 문의해주세요."),
    NO_EXIST_OAUTH(HttpStatus.NOT_FOUND, false, 406, "소셜 로그인 정보가 존재하지 않습니다."),

    /**
     * 900: 기타 에러
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, false, 900, "Internal server error"),
    SSE_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, false, 901, "알림 전송에 실패하였습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, false, 902, "유효하지 입력입니다"),
    FAILED_TO_SAVE(HttpStatus.INTERNAL_SERVER_ERROR, false, 903, "저장에 실패했습니다."),

    /**
     * 2000: funding service error
     */
    NO_EXIST_FUNDING(HttpStatus.NOT_FOUND, false, 2001, "공모 상품이 존재하지 않습니다."),
    FAILED_TO_UPDATE(HttpStatus.INTERNAL_SERVER_ERROR, false, 2002, "수정에 실패했습니다."),
    FAIL_TO_JOIN(HttpStatus.INTERNAL_SERVER_ERROR, false, 2003, "공모 참여에 실패했습니다."),
    NO_MORE_PIECES(HttpStatus.INTERNAL_SERVER_ERROR, false, 2004, "잔여 조각이 없습니다."),
    NO_PARTICIPATED(HttpStatus.INTERNAL_SERVER_ERROR, false, 2005, "잔여 조각이 없습니다."),
    FUNDING_ALREADY_CLOSED(HttpStatus.BAD_REQUEST, false, 2006, "공모 참여가 마감되었습니다."),
    CANNOT_CANCEL_PARTICIPATION(HttpStatus.BAD_REQUEST, false, 2007, "공모 취소가 불가능한 상태입니다."),
    UNAUTHORIZED_PARTICIPATION(HttpStatus.UNAUTHORIZED, false, 2008, "공모에 참여할 권한이 없습니다."),
    INVALID_FUNDING_STATUS(HttpStatus.BAD_REQUEST, false, 2009, "공모 상태가 유효하지 않습니다.");




    private final HttpStatusCode httpStatusCode;
    private final boolean isSuccess;
    private final int code;
    private final String message;

}
