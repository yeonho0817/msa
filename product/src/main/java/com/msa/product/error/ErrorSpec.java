package com.msa.product.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorSpec {
    AccessDeniedException(FORBIDDEN, "권한이 없습니다."),
    InvalidTokenError(UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    InvalidRefreshTokenError(UNAUTHORIZED, "유효하지 않은 refresh 토큰입니다."),
    UnRegisteredTokenError(UNAUTHORIZED, "등록되지 않은 토큰입니다."),

    InvalidPasswordError(BAD_REQUEST, "패스워드가 일치하지 않습니다."),
    AlreadyExistAccount(BAD_REQUEST, "이미 존재하는 ID 입니다."),
    NotExistAccount(BAD_REQUEST, "존재하지 않는 계정입니다."),
    InvalidParameterValue(BAD_REQUEST, "잘못된 요청입니다."),

    InternalServerError(INTERNAL_SERVER_ERROR, "서버 오류입니다. %s"),
    NotFoundError(NOT_FOUND, "존재하지 않는 API 입니다."),
    ExternalApiServiceExecuteFailedError(SERVICE_UNAVAILABLE, "외부 API 서비스 호출 중에 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
