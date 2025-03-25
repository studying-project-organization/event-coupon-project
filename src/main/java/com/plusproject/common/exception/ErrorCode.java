package com.plusproject.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // A

    // B
    BAD_REQUEST_EXIST_EMAIL(BAD_REQUEST, "이미 존재하는 이메일입니다."),
    BAD_REQUEST_NOT_SIGN_UP_USER(BAD_REQUEST, "가입되지 않은 유저입니다."),
    BAD_REQUEST_DO_NOT_MATCH_OLD_AND_NEW_PASSWORD(BAD_REQUEST, "새 비밀번호는 기존 비밀번호와 같을 수 없습니다."),
    BAD_REQUEST_NOT_MATCH_OLD_PASSWORD(BAD_REQUEST, "기존 비밀번호와 일치하지 않습니다."),
    BAD_REQUEST_ALREADY_HAVE_COUPON(BAD_REQUEST, "이미 해당 쿠폰을 가지고 있습니다."),
    BAD_REQUEST_QUANTITY_IS_ZERO(BAD_REQUEST, "해당 쿠폰의 모든 수량이 소모되었습니다."),
    // C

    // F
    FORBIDDEN_USER_ROLE_ADMIN(FORBIDDEN, "관리자 권한이 필요합니다."),
    FORBIDDEN_USER_ROLE_USER(FORBIDDEN, "사용자 권한이 필요합니다."),

    // I
    INVALID_USER_TYPE(BAD_REQUEST, "유효하지 않은 사용자 유형(UserType)입니다."),
    INTERNAL_SERVER_ERROR_NOT_FOUND_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "토큰을 찾을 수 없습니다."),

    // N
    NOT_FOUND_USER(NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    NOT_FOUND_COUPON(NOT_FOUND, "해당 쿠폰을 찾을 수 없습니다."),
    // U
    UNAUTHORIZED_AUTH_WITH_AUTH_TYPE(UNAUTHORIZED, "@Auth와 AuthUser 타입은 함께 사용되어야 합니다."),
    UNAUTHORIZED_WRONG_PASSWORD(UNAUTHORIZED, "잘못된 비밀번호입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
