package com.plusproject.domain.usercoupon.controller;

import com.plusproject.common.annotation.Auth;
import com.plusproject.common.dto.ApiResponse;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.domain.usercoupon.dto.request.UserCouponeGetRequest;
import com.plusproject.domain.usercoupon.dto.response.UserCouponResponse;
import com.plusproject.domain.usercoupon.service.UserCouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserCouponController {
    private final UserCouponService userCouponService;

    @PostMapping("/api/v1/users/coupons")
    public ApiResponse<UserCouponResponse> getUserCoupon(
            @Auth AuthUser authUser,
            @Valid @RequestBody UserCouponeGetRequest userCouponeGetRequest
            ) {
        UserCouponResponse userCouponResponse = userCouponService.getUserCoupon(authUser.getId(), userCouponeGetRequest);
        return ApiResponse.success(userCouponResponse, "쿠폰이 생성되었습니다.");
    }

    @GetMapping("/api/v1/users/coupons")
    public ApiResponse<List<UserCouponResponse>> getUserCoupons(
            @Auth AuthUser authUser
    ) {
        List<UserCouponResponse> userCouponResponses = userCouponService.getUserCoupons(authUser.getId());
        return ApiResponse.success(userCouponResponses);
    }
}
