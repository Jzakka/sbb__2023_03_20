package com.mysite.sbb.answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    @Transactional
    @Modifying
    @Query(value = "ALTER TABLE answer AUTO_INCREMENT = 1", nativeQuery = true)
    void clearAutoIncrement();

    Page<Answer> findAll(Pageable pageable);

    @Query(value =
            "select a.id, a.content, a.create_date, a.question_id, a.author_id, a.modified_date\n" +
                    "from question as q\n" +
                    "inner join\n" +
                    "(select a.id, a.content, a.create_date, a.question_id, a.author_id, a.modified_date, ifnull(ans_cnt.vote_cnt, 0) as vote_cnt\n" +
                    "from answer as a\n" +
                    "left join \n" +
                    "(select answer_id, count(voter_id) as vote_cnt\n" +
                    "from answer_voter\n" +
                    "group by answer_id) as ans_cnt on a.id = ans_cnt.answer_id) as a on q.id = a.question_id\n" +
                    "where q.id = :qid\n" +
                    "order by vote_cnt desc, create_date desc\n",
            nativeQuery = true
    )
    Page<Answer> findAll(@Param("qid") Integer qid, Pageable pageable);
}
