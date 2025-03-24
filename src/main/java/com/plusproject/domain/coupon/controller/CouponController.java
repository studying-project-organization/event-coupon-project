package com.plusproject.domain.coupon.controller;

import com.plusproject.common.annotation.AdminOnly;
import com.plusproject.common.dto.ApiResponse;
import com.plusproject.domain.coupon.dto.request.CreateCouponRequest;
import com.plusproject.domain.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponService couponService;

    @AdminOnly
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        couponService.createCoupon(request);
        return ResponseEntity.ok(ApiResponse.success("쿠폰이 생성되었습니다."));
    }

}
