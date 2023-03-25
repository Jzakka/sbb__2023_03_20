package com.mysite.sbb.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResetForm {

    @NotEmpty(message = "가입 시 입력했던 이름을 입력하세요.")
    private String username;

    @Email(message = "이메일 형식으로 입력하세요")
    private String email;
}
