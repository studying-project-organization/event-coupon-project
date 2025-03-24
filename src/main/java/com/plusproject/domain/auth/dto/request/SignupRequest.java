package com.plusproject.domain.auth.dto.request;

import com.plusproject.common.Const;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "이메일은 필수값입니다.")
    @Email(message = "이메일은 형식에 맞아야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수값입니다.")
    @Pattern(
            regexp = Const.PASSWORD_PATTERN,
            message = "비밀번호는 대소문자 영문, 숫자, 특수문자를 최소 1글자씩 포함하며, 8자 이상 20자 이하이어야 합니다."
    )
    private String password;

    @NotBlank(message = "이름은 필수값입니다.")
    @Size(max = 20, message = "이름은 최대 20자 입니다.")
    private String nickname;

    @NotBlank(message = "주소는 필수값입니다.")
    @Size(max = 255, message = "주소는 최대 255자 입니다.")
    private String address;
}