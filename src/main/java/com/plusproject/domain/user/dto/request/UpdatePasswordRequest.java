package com.plusproject.domain.user.dto.request;

import com.plusproject.common.Const;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdatePasswordRequest {

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    private String oldPassword;

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    @Pattern(
        regexp = Const.PASSWORD_PATTERN,
        message = "비밀번호 형식이 올바르지 않습니다."
    )
    private String newPassword;

    @Builder
    private UpdatePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

}
