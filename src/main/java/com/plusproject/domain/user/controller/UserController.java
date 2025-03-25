package com.plusproject.domain.user.controller;

import com.plusproject.common.annotation.UserId;
import com.plusproject.common.dto.ApiResponse;
import com.plusproject.domain.user.dto.request.CreateUserRequest;
import com.plusproject.domain.user.dto.request.LoginRequest;
import com.plusproject.domain.user.dto.request.UpdatePasswordRequest;
import com.plusproject.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userServ;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody CreateUserRequest req) {

        return ResponseEntity.ok().body(userServ.createUser(req));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<String>> signin(@Valid @RequestBody LoginRequest req){

        return ResponseEntity.ok().body(userServ.login(req));
    }

    @PutMapping("/users/{userId}/admin")
    public ResponseEntity<ApiResponse<Void>> adminGrant(@UserId Long loginUserId, @PathVariable Long userId) {
        return ResponseEntity.ok().body(userServ.updateUserRole(loginUserId, userId));
    }

    @PutMapping("/users")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@UserId Long loginUserId,@Valid @RequestBody UpdatePasswordRequest password) {
        return ResponseEntity.ok().body(userServ.updatePassword(loginUserId, password));
    }
}
