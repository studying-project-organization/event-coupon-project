package com.plusproject.domain.usercoupon.enums;

import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;

import java.util.Arrays;

public enum UserRole {
    USER, ADMIN;

    public static UserRole of(String type) {
        return Arrays.stream(UserRole.values())
            .filter(t -> t.name().equalsIgnoreCase(type))
            .findFirst()
            .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_USER_TYPE));
    }
}
