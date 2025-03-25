package com.plusproject.domain.user.controller;

import com.plusproject.common.annotation.Auth;
import com.plusproject.common.annotation.UserId;
import com.plusproject.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class UserController {

    @PostMapping("signup")
    public ResponseEntity<ApiResponse> signup(@UserId Long userId) {

    }
}
