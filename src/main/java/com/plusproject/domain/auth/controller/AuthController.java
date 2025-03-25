package com.plusproject.domain.auth.controller;

import com.plusproject.common.dto.ApiResponse;
import com.plusproject.domain.auth.dto.request.SigninRequest;
import com.plusproject.domain.auth.dto.request.SignupRequest;
import com.plusproject.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(ApiResponse.success(authService.signup(signupRequest), "회원 가입에 성공했습니다."));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse> signin(@Valid @RequestBody SigninRequest signinRequest) {
        return ResponseEntity.ok(ApiResponse.success(authService.signin(signinRequest), "로그인에 성공했습니다."));
    }
}