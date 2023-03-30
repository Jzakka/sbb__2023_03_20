package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.mysite.sbb.comment.QComment.comment;

@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Comment> findAllOfQuestion(Question question, Pageable pageable) {
        List<Comment> comments = jpaQueryFactory.selectFrom(comment)
                .where(comment.question.eq(question))
                .orderBy(comment.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(comments, pageable,
                jpaQueryFactory.select(comment.count())
                        .from(comment)
                        .where(comment.question.eq(question))
                        .fetchFirst());
    }

    @Override
    public Page<Comment> findAllOfAnswer(Answer answer, Pageable pageable) {
        List<Comment> comments = jpaQueryFactory.selectFrom(comment)
                .where(comment.answer.eq(answer))
                .orderBy(comment.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(comments, pageable,
                jpaQueryFactory.select(comment.count())
                        .from(comment)
                        .where(comment.answer.eq(answer))
                        .fetchFirst());
    }
}
