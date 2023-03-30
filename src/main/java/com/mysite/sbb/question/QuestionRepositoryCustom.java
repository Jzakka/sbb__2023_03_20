package com.mysite.sbb.question;

import com.mysite.sbb.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionRepositoryCustom {
    Page<Question> findAllByKeyword(String kw, Pageable pageable);
    Page<Question> findAllByKeyword(String kw, Pageable pageable, Category category);
}
