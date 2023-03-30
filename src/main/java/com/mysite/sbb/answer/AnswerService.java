package com.mysite.sbb.answer;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class AnswerService {
    private final AnswerRepository answerRepository;

    public Answer getAnswer(Integer id) {
        Optional<Answer> answerOptional = answerRepository.findById(id);
        if (answerOptional.isPresent()) {
            return answerOptional.get();
        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifiedDate(LocalDateTime.now());
        answerRepository.save(answer);
    }

    public Answer create(Question question, String content, SiteUser author) {
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        answer.setAuthor(author);
        return answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }

    public void vote(Answer answer, SiteUser user) {
        answer.getVoter().add(user);
        answerRepository.save(answer);
    }

    public Page<Answer> getList(Question question, int page) {
        List<Sort.Order> sorts = new ArrayList<>();
        PageRequest pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return answerRepository.findAll(question.getId(), pageable);
    }

    public Page<Answer> getList(Question question) {
        return getList(question, 0);
    }

    public Page<Answer> getList(Integer page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return answerRepository.findAll(pageable);
    }
}
