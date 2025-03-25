package com.plusproject.domain.usercoupon.controller;

import com.plusproject.common.annotation.Auth;
import com.plusproject.common.dto.ApiResponse;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.domain.usercoupon.repository.UserCouponRepository;
import com.plusproject.domain.usercoupon.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserCouponController {

    private final UserCouponService userCouponServ;

    @PostMapping("/users/coupons")
    public ApiResponse<Void> couponIssue(@Auth AuthUser user) {
        userCouponServ.couponIssue(user.getUserId());

        return ApiResponse.success("쿠폰 발급에 성공했습니다.");
    }
}
