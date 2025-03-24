package com.plusproject.domain.coupon.controller;

import com.plusproject.common.annotation.Admin;
import com.plusproject.common.dto.ApiResponse;
import com.plusproject.domain.coupon.dto.request.CouponCreateRequest;
import com.plusproject.domain.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Admin
    @PostMapping
    public ResponseEntity<ApiResponse> createCoupon(@Valid @RequestBody CouponCreateRequest couponCreateRequest) {
        couponService.createCoupon(couponCreateRequest);
        return ResponseEntity.ok(ApiResponse.success("쿠폰이 생성되었습니다."));
    }
}
