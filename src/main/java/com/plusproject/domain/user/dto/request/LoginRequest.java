package com.plusproject.domain.user.dto.request;

import com.plusproject.common.Const;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LoginRequest {

    @Email
    private String email;

    @Pattern(regexp = Const.PASSWORD_PATTERN)
    private String password;
}
