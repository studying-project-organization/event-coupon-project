package com.plusproject.domain.usercoupon.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class IssuedCouponRequest {

    @NotNull(message = "CouponId 입력은 필수입니다.")
    private Long couponId;

}
