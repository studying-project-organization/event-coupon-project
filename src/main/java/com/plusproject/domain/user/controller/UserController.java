package com.plusproject.domain.user.controller;

import com.plusproject.common.annotation.Auth;
import com.plusproject.common.annotation.User;
import com.plusproject.common.dto.ApiResponse;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.domain.user.dto.request.UserPasswordUpdateRequest;
import com.plusproject.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping
    public ResponseEntity<ApiResponse> updatePassword(@Auth AuthUser authUser,
                                                      @Valid @RequestBody UserPasswordUpdateRequest userPasswordUpdateRequest) {
        userService.updatePassword(authUser, userPasswordUpdateRequest);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 변경에 성공했습니다."));
    }

    @User
    @PostMapping("/coupons/{couponId}")
    public ResponseEntity<ApiResponse> receiveCoupon(@Auth AuthUser authUser, @PathVariable Long couponId) {
        userService.receiveCoupon(authUser, couponId);
        return ResponseEntity.ok(ApiResponse.success("쿠폰 발급에 성공했습니다."));
    }

    @User
    @GetMapping("/coupons")
    public ResponseEntity<ApiResponse> findCoupons(@Auth AuthUser authUser) {
        return ResponseEntity.ok(ApiResponse.success(userService.findCoupons(authUser), "쿠폰 조회에 성공했습니다."));
    }
}
