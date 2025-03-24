package com.plusproject.domain.user.controller;

import com.plusproject.common.annotation.Admin;
import com.plusproject.common.annotation.Auth;
import com.plusproject.common.dto.ApiResponse;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.domain.user.dto.request.ChangeUserRoleRequest;
import com.plusproject.domain.user.service.UserAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAdminService userAdminService;

    @Admin
    @PutMapping("/{userId}/admin")
    public ResponseEntity<ApiResponse> updateUserRole(@PathVariable Long userId,
                                                      @Valid @RequestBody ChangeUserRoleRequest userRoleRequest) {
        userAdminService.changeUserRole(userId, userRoleRequest);

        return ResponseEntity.ok(ApiResponse.success("유저 권한 변경에 성공했습니다."));
    }
}
