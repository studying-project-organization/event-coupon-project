package com.plusproject.domain.usercoupon.controller;

import com.plusproject.common.annotation.Auth;
import com.plusproject.common.dto.ApiResponse;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.domain.usercoupon.dto.request.IssuedCouponRequest;
import com.plusproject.domain.usercoupon.dto.response.UserCouponResponse;
import com.plusproject.domain.usercoupon.service.UserCouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user-coupon")
public class UserCouponController {

    private final UserCouponService userCouponService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> issuedCoupon(
        @Auth AuthUser authUser,
        @Valid @RequestBody IssuedCouponRequest request
    ) throws InterruptedException {
        userCouponService.issuedCoupon(authUser, request);
        return ResponseEntity.ok(ApiResponse.success("쿠폰 발급에 성공했습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserCouponResponse>>> findAllUserCoupon(
        @Auth AuthUser authUser
    ) {
        List<UserCouponResponse> response = userCouponService.findAllUserCoupon(authUser);
        return ResponseEntity.ok(ApiResponse.success(response, "쿠폰 조회에 성공했습니다."));
    }

}
