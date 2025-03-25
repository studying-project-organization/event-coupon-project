package com.plusproject.domain.UserCoupon.Controller;

import com.plusproject.common.annotation.Auth;
import com.plusproject.common.annotation.User;
import com.plusproject.common.dto.ApiResponse;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.domain.UserCoupon.Service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/coupons")
@RequiredArgsConstructor
public class UserCouponController {

    private final UserCouponService userCouponService;

    @User
    @PostMapping("/{couponId}")
    public ResponseEntity<ApiResponse> receiveCoupon(@Auth AuthUser authUser, @PathVariable Long couponId) {
        userCouponService.receiveCoupon(authUser, couponId);
        return ResponseEntity.ok(ApiResponse.success("쿠폰 발급에 성공했습니다."));
    }

    @User
    @GetMapping
    public ResponseEntity<ApiResponse> findCoupons(@Auth AuthUser authUser) {
        return ResponseEntity.ok(ApiResponse.success(userCouponService.findCoupons(authUser), "쿠폰 조회에 성공했습니다."));
    }
}
