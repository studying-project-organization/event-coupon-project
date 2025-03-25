package com.plusproject.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserResponse {

    private final Long id;
    private final String email;
    private final String userRole;

    public UserResponse(Long id, String email, String userRole) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }
}
