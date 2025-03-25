package com.plusproject.domain.auth.controller;

import com.plusproject.common.dto.ApiResponse;
import com.plusproject.domain.auth.dto.request.SigninRequest;
import com.plusproject.domain.auth.dto.request.SignupRequest;
import com.plusproject.domain.auth.dto.response.SigninResponse;
import com.plusproject.domain.auth.service.AuthService;
import com.plusproject.domain.user.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/api/v1/auth/signup")
    public ApiResponse<UserResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return ApiResponse.success("회원 가입이 성공적으로 완료되었습니다.");
    }

    @PostMapping("/api/v1/auth/signin")
    public ApiResponse<SigninResponse> signin(@Valid @RequestBody SigninRequest signinRequest) {
        SigninResponse signinResponse = authService.signin(signinRequest);
        return ApiResponse.success(signinResponse);
    }
}
