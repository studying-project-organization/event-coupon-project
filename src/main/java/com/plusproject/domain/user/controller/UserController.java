package com.plusproject.domain.user.controller;

import com.plusproject.common.annotation.Auth;
import com.plusproject.common.dto.ApiResponse;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.domain.user.dto.request.UserChangePasswordRequest;
import com.plusproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/api/v1/users")
    public ApiResponse<Void> changePassword(
            @Auth AuthUser authUser,
            @RequestBody UserChangePasswordRequest userChangePasswordRequest
    ) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
        return ApiResponse.success("비밀번호가 성공적으로 변경되었습니다.");
    }
}
