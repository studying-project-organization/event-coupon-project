package com.plusproject.domain.user.dto.request;

import com.plusproject.common.Const;
import com.plusproject.domain.user.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateUserRequest {

    @Email
    @NotBlank
    private String email;

    @Size(max = 15)
    @NotBlank
    private String nickname;

    @Pattern(regexp = Const.PASSWORD_PATTERN)
    private String password;

    @NotBlank
    private String address;
}
