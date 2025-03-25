package com.plusproject.domain.usercoupon.enums;

import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;

import java.util.Arrays;

public enum CouponStatusRole {
    BEFORE_USE, AFTER_USE;

    public static com.plusproject.domain.usercoupon.enums.CouponStatusRole of(String type) {
        return Arrays.stream(com.plusproject.domain.usercoupon.enums.CouponStatusRole.values())
            .filter(t -> t.name().equalsIgnoreCase(type))
            .findFirst()
            .orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_COUPON_TYPE));
    }
}
