package com.plusproject.domain.coupon.controller;

import com.plusproject.common.dto.ApiResponse;
import com.plusproject.domain.coupon.dto.request.CreateCouponRequest;
import com.plusproject.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponServ;

    @PostMapping("/users/coupons")
    public ResponseEntity<ApiResponse<Void>> createCoupon(CreateCouponRequest req) {
        couponServ.createCoupon(req);

        return ResponseEntity.ok().body(ApiResponse.success("쿠폰이 생성되었습니다."));
    }
}
