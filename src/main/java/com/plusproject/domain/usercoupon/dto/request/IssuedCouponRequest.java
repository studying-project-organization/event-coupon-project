package com.plusproject.domain.usercoupon.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IssuedCouponRequest {

    @NotNull(message = "CouponId 입력은 필수입니다.")
    private Long couponId;

    @Builder
    private IssuedCouponRequest(Long couponId) {
        this.couponId = couponId;
    }

}
