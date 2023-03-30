package com.mysite.sbb.question;

import com.mysite.sbb.answer.QAnswer;
import com.mysite.sbb.category.Category;
import com.mysite.sbb.user.QSiteUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.mysite.sbb.answer.QAnswer.answer;
import static com.mysite.sbb.question.QQuestion.*;

@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Question> findAllByKeyword(String kw, Pageable pageable) {
        QSiteUser siteUser1 = new QSiteUser("u1");
        QSiteUser siteUser2 = new QSiteUser("u2");
        kw = '%' + kw + '%';

        List<Question> content = queryFactory.selectDistinct(question)
                .from(question)
                .leftJoin(question.author, siteUser1)
                .leftJoin(question.answerList, answer)
                .leftJoin(answer.author, siteUser2)
                .where(question.subject.like(kw)
                        .or(siteUser1.name.like(kw))
                        .or(answer.content.like(kw))
                        .or(siteUser2.name.like(kw)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(question.createDate.desc())
                .fetch();

        return new PageImpl<>(content, pageable, queryFactory.select(question.count()).from(question).fetchFirst());
    }

    @Override
    public Page<Question> findAllByKeyword(String kw, Pageable pageable, Category category) {
        QSiteUser siteUser1 = new QSiteUser("u1");
        QSiteUser siteUser2 = new QSiteUser("u2");
        kw = '%' + kw + '%';

        List<Question> content = queryFactory.selectDistinct(question)
                .from(question)
                .leftJoin(question.author, siteUser1)
                .leftJoin(question.answerList, answer)
                .leftJoin(answer.author, siteUser2)
                .where(question.subject.like(kw)
                        .or(siteUser1.name.like(kw))
                        .or(answer.content.like(kw))
                        .or(siteUser2.name.like(kw))
                        .and(question.category.eq(category)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(question.createDate.desc())
                .fetch();

        return new PageImpl<>(content, pageable,
                queryFactory
                        .select(question.count())
                        .from(question)
                        .where(question.category.eq(category))
                        .fetchFirst());
    }
}
