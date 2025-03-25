package com.plusproject.domain.user.controller;

import com.plusproject.common.annotation.Auth;
import com.plusproject.common.annotation.UserId;
import com.plusproject.common.dto.ApiResponse;
import com.plusproject.common.dto.AuthUser;
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
        userServ.createUser(req);

        return ResponseEntity.ok().body(ApiResponse.success("유저 권한 변경에 성공했습니다."));
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<Void>> signin(@Valid @RequestBody LoginRequest req){
        String token = userServ.login(req);
        return ResponseEntity.ok().body(ApiResponse.success(token));
    }

    @PutMapping("/users/{userId}/admin")
    public ResponseEntity<ApiResponse<Void>> adminGrant(@Auth AuthUser user, @PathVariable Long userId) {
        userServ.updateUserRole(user.getUserId(), userId);
        return ResponseEntity.ok().body(ApiResponse.success("유저 권한 변경에 성공했습니다."));
    }

    @PutMapping("/users")
    public ResponseEntity<ApiResponse<Void>> updatePassword(@Auth AuthUser user,@Valid @RequestBody UpdatePasswordRequest password) {
        userServ.updatePassword(user.getUserId(), password);
        return ResponseEntity.ok().body(ApiResponse.success("비밀번호 변경에 성공했습니다."));
    }
}
