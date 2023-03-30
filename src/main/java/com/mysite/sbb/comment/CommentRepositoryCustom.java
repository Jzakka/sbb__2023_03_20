package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepositoryCustom {

    Page<Comment> findAllOfQuestion(Question question, Pageable pageable);

    Page<Comment> findAllOfAnswer(Answer answer, Pageable pageable);
}
