package com.mysite.sbb;

import com.mysite.sbb.category.Category;
import com.mysite.sbb.category.CategoryService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import com.mysite.sbb.question.QuestionService;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
public class QuerydslTest {
    @Autowired
    QuestionService questionService;

    @Autowired
    CategoryService categoryService;

    @Test
    void findAllByKeyword() {
        Page<Question> question = questionService.getList( 0,"question");
        Assertions.assertThat(question.getContent().size()).isEqualTo(10);
    }

    @Test
    void findAllByKeywordAndCategory() {
        Optional<Category> category2 = categoryService.get("카테고리2");

        Page<Question> question = questionService.getList( 0,"", category2);
        Assertions.assertThat(question.getContent().size()).isEqualTo(8);
    }
}
