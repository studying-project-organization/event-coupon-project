package com.plusproject.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {

    // A
    ACQUISITION_FAILED_LOCK(CONFLICT, "락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요."),

    // D
    DUPLICATE_EMAIL(CONFLICT, "이미 가입되어있는 이메일 입니다."),
    DUPLICATE_COUPON_ISSUANCE(CONFLICT, "이미 발급받은 쿠폰입니다."),

    // E
    EXPIRED_JWT_TOKEN(UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    EXHAUSETD_COUPON(BAD_REQUEST, "쿠폰이 모두 소진되었습니다."),

    // F
    FORBIDDEN_ADMIN_ONLY(FORBIDDEN, "ADMIN 권한을 가진 유저만 접근할 수 있습니다."),
    FORBIDDEN_USER_ONLY(FORBIDDEN, "일반 유저만 접근할 수 있습니다."),

    // I
    INVALID_AUTH_ANNOTATION_USAGE(BAD_REQUEST, "@Auth와 AuthUser 타입은 함께 사용되어야 합니다."),
    INVALID_USER_TYPE(BAD_REQUEST, "유효하지 않은 사용자 유형(UserType)입니다."),
    INVALID_JWT_TOKEN(BAD_REQUEST, "유효하지 않은 JWT 토큰입니다."),
    INVALID_JWT_SIGNATURE(UNAUTHORIZED, "유효하지 않은 JWT 서명입니다."),
    INVALID_SELF_ROLE_UPDATE(BAD_REQUEST, "자기 자신의 권한은 수정할 수 없습니다."),
    INVALID_ADMIN_ROLE_UPDATE(BAD_REQUEST, "같은 관리자의 권한은 수정할 수 없습니다."),
    INCORRECT_PASSWORD(UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),

    // M
    MISSING_JWT_TOKEN(BAD_REQUEST, "토큰이 요청에 포함되지 않았습니다."),

    // N
    NOT_FOUND_USER(NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    NOT_FOUND_COUPON(NOT_FOUND, "해당 쿠폰을 찾을 수 없습니다."),
    NOT_FOUND_USER_COUPON(NOT_FOUND, "해당 유저쿠폰 테이블을 찾을 수 없습니다."),

    // R
    REQUIRED_JWT_TOKEN(BAD_REQUEST, "JWT 토큰이 필요합니다."),

    // S
    SAME_AS_OLD_PASSWORD(CONFLICT, "기존 비밀번호와 새 비밀번호가 같습니다."),

    // U
    UNSUPPORTED_JWT_TOKEN(BAD_REQUEST, "지원되지 않는 JWT 토큰입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
