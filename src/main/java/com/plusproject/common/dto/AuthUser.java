package com.plusproject.common.dto;

import com.plusproject.domain.user.enums.UserRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AuthUser {

    private Long userId;
    private UserRole userRole;

    public AuthUser(Long userId, UserRole userRole) {
        this.userId = userId;
        this.userRole = userRole;
    }
}
