package com.plusproject.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Getter
public enum ErrorCode {

    // A

    // B

    // C

    // E
    EXPIRED_TOKEN(UNAUTHORIZED, "토큰이 만료되었습니다."),

    // I,
    INVALID_USER_TYPE(BAD_REQUEST, "유효하지 않은 사용자 유형(UserType)입니다."),
    INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰입니다."),


    // N,
    NOT_FOUND_USER(NOT_FOUND, "해당 유저를 찾을 수 없습니다."),

    // T
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "토큰이 제공되지 않았습니다."),

    ;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
