package com.plusproject.domain.usercoupon.controller;

import com.plusproject.common.annotation.Auth;
import com.plusproject.common.dto.ApiResponse;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.domain.user.dto.response.FindUserCouponResponse;
import com.plusproject.domain.usercoupon.entity.UserCoupons;
import com.plusproject.domain.usercoupon.repository.UserCouponRepository;
import com.plusproject.domain.usercoupon.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserCouponController {

    private final UserCouponService userCouponServ;

    @PostMapping("/users/coupons")
    public ResponseEntity<ApiResponse<Void>> couponIssue(@Auth AuthUser user) {
        userCouponServ.couponIssue(user.getUserId());

        return ResponseEntity.ok().body(ApiResponse.success("쿠폰 발급에 성공했습니다."));
    }

    @GetMapping("/users/coupons")
    public ResponseEntity<ApiResponse<List<FindUserCouponResponse>>> findCoupon(@Auth AuthUser user) {
        Long userId = user.getUserId();

        List<FindUserCouponResponse> allUserCoupons = userCouponServ.findAllUserCoupons(userId);

        return ResponseEntity.ok().body(ApiResponse.success(allUserCoupons, "쿠폰 조회에 성공했습니다."));
    }
}
