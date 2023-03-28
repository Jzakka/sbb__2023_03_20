package com.mysite.sbb;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.comment.Comment;
import com.mysite.sbb.comment.CommentRepository;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Test
    void testJpa() {
        Answer answer = answerRepository.findById(200).get();
        Question question = questionRepository.findById(100).get();
        for (int i = 0; i < 250; i++) {

            Comment comment2 = new Comment();
            comment2.setAuthor(userRepository.findByName("user1").get());
            comment2.setContent("No hi");
            comment2.setAnswer(answer);
            comment2.setCreateDate(LocalDateTime.now());
            commentRepository.save(comment2);
        }
    }
}