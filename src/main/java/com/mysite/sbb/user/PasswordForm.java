package com.mysite.sbb.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordForm {
    @NotEmpty(message = "비밀번호 입력은 필수입니다.")
    private String password;

    private String token;
}
