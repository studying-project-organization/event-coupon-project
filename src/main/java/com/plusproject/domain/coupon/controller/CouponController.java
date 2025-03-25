package com.plusproject.domain.coupon.controller;

import com.plusproject.common.annotation.AuthCheck;
import com.plusproject.common.dto.ApiResponse;
import com.plusproject.domain.coupon.dto.request.CouponCreateRequest;
import com.plusproject.domain.coupon.dto.response.CouponResponse;
import com.plusproject.domain.coupon.service.CouponService;
import com.plusproject.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @AuthCheck(UserRole.ADMIN)
    @PostMapping("/api/v1/coupons")
    public ApiResponse<CouponResponse> createCoupon(
            @RequestBody CouponCreateRequest couponCreateRequest
    ) {
        CouponResponse couponResponse = couponService.createCoupon(couponCreateRequest);
        return ApiResponse.success(couponResponse, "쿠폰이 생성되었습니다.");
    }
}
