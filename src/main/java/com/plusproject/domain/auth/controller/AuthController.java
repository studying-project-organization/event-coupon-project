package com.plusproject.domain.auth.controller;

import com.plusproject.common.dto.ApiResponse;
import com.plusproject.domain.auth.dto.request.SigninRequest;
import com.plusproject.domain.auth.dto.request.SignupRequest;
import com.plusproject.domain.auth.dto.response.AccessTokenResponse;
import com.plusproject.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok(ApiResponse.success("회원가입에 성공했습니다."));
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> signin(@Valid @RequestBody SigninRequest request) {
        AccessTokenResponse response = authService.signin(request);
        return ResponseEntity.ok(ApiResponse.success(response,"로그인에 성공했습니다."));
    }
}
