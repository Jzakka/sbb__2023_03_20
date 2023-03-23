package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public Comment create(Question question, String content, SiteUser user) {
        Comment comment = new Comment();
        comment.setCreateDate(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setContent(content);
        question.addComment(comment);
        return commentRepository.save(comment);
    }

    public Comment create(Answer answer, String content, SiteUser user) {
        Comment comment = new Comment();
        comment.setCreateDate(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setContent(content);
        answer.addComment(comment);
        return commentRepository.save(comment);
    }
}
