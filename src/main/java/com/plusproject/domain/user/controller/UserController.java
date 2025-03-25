package com.plusproject.domain.user.controller;

import com.plusproject.common.annotation.AdminOnly;
import com.plusproject.common.annotation.Auth;
import com.plusproject.common.dto.ApiResponse;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.domain.user.dto.request.UpdateAdminRequest;
import com.plusproject.domain.user.dto.request.UpdatePasswordRequest;
import com.plusproject.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @AdminOnly
    @PutMapping("/{userId}/admin")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
        @Auth AuthUser authUser,
        @PathVariable("userId") Long userId,
        @Valid @RequestBody UpdateAdminRequest request
    ) {
        userService.updateUserRole(authUser, userId, request);
        return ResponseEntity.ok(ApiResponse.success("유저 권한 변경에 성공했습니다."));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Void>> updatePassword(
        @Auth AuthUser authUser,
        @Valid @RequestBody UpdatePasswordRequest request
    ) {
        userService.updatePassword(authUser, request);
        return ResponseEntity.ok(ApiResponse.success("비밀번호 변경에 성공했습니다."));
    }

}
