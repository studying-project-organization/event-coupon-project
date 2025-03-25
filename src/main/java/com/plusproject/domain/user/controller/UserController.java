package com.plusproject.domain.user.controller;

import com.plusproject.common.annotation.Auth;
import com.plusproject.common.dto.ApiResponse;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.domain.user.dto.request.UserPasswordUpdateRequest;
import com.plusproject.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
