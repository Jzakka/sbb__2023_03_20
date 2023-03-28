package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findAll(Pageable pageable);

    @Query(
            "select c " +
                    "from Comment c " +
                    "where c.question = :question"
    )
    Page<Comment> findAllOfQuestion(@Param("question") Question question, Pageable pageable);

    @Query(
            "select c " +
                    "from Comment c " +
                    "where c.answer = :answer"
    )
    Page<Comment> findAllOfAnswer(@Param("answer") Answer answer, Pageable pageable);
}
