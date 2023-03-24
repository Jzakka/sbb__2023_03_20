package com.mysite.sbb.category;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryForm {
    @NotEmpty(message="카테고리 이름을 입력해주세요.")
    private String name;
}
