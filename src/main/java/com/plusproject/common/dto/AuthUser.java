package com.plusproject.common.dto;

import com.plusproject.domain.user.enums.UserRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AuthUser {

    private Long id;
    private String email;
    private UserRole userRole;

    @Builder
    public AuthUser(Long id, String email, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }
}
