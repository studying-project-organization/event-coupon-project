package com.plusproject.domain.user.dto.request;

import com.plusproject.common.Const;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdatePasswordRequest {

    @Pattern(regexp = Const.PASSWORD_PATTERN)
    private String password;
}
