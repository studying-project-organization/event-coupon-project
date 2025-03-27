package com.plusproject.domain.usercoupon.controller;

import com.plusproject.common.annotation.Auth;
import com.plusproject.common.annotation.RedissonDistributedLock;
import com.plusproject.common.dto.ApiResponse;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.domain.usercoupon.dto.request.IssuedCouponRequest;
import com.plusproject.domain.usercoupon.dto.response.UserCouponResponse;
import com.plusproject.domain.usercoupon.service.UserCouponService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user-coupon")
public class UserCouponController {

    private final UserCouponService userCouponService;

    @PostMapping
    @RedissonDistributedLock(key = "#request.couponId")
    public ResponseEntity<ApiResponse<Void>> issuedCoupon(
        @Auth AuthUser authUser,
        @Valid @RequestBody IssuedCouponRequest request
    ) {
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
