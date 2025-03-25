package com.plusproject.domain.coupon.controller;

import com.plusproject.common.dto.ApiResponse;
import com.plusproject.domain.coupon.dto.request.CreateCouponRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class CouponController {

    @PostMapping("/users/coupons")
    public ResponseEntity<ApiResponse<Void>> createCoupon(CreateCouponRequest req) {

    }
}
