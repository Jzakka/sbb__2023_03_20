package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {

    Page<Comment> findAllOfQuestion(Question question, Pageable pageable);

    Page<Comment> findAllOfAnswer(Answer answer, Pageable pageable);
}
