package com.plusproject.domain.user.dto.request;

import com.plusproject.domain.user.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateAdminRequest {

    @NotNull(message = "유저 Role 입력은 필수입니다.")
    private UserRole userRole;

}
