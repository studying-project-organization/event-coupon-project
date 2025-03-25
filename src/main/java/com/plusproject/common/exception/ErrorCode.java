package com.plusproject.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
public enum ErrorCode {

    // A

    // B

    // C

    // I
    INVALID_USER_TYPE(BAD_REQUEST, "유효하지 않은 사용자 유형(UserType)입니다."),
    INVALID_PASSWORD(BAD_REQUEST, "잘못된 비밀번호 입니다." ),
    INVALID_COUPON_TYPE(BAD_REQUEST, "유효하지 않은 쿠폰유형 입니다."),

    // N
    NOT_FOUND_USER(NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    NOT_FOUND_TOKEN(NOT_FOUND, "발급된 토큰이 없습니다."),
    NOT_FOUND_COUPON(NOT_FOUND, "쿠폰을 찾을수 없습니다."),

    // R
    RECEIVED_SAME_EMAIL(BAD_REQUEST, "동일한 이메일이 있습니다."),

    // S
    SAME_PASSWORD(BAD_REQUEST, "새 비밀번호는 기존 비밀번호와 같을 수 없습니다."),

    // U
    UNAUTHORIZED(BAD_REQUEST, "권환이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
