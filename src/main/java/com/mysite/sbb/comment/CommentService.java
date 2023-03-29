package com.mysite.sbb.comment;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Page<Comment> getList(Integer page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return commentRepository.findAll(pageable);
    }

    public Comment getComment(Integer id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isPresent()) {
            return commentOptional.get();
        }
        throw new DataNotFoundException("Comment not found");
    }

    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    public Page<Comment> getList(Question question, Integer page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return commentRepository.findAllOfQuestion(question, pageable);
    }

    public Page<Comment> getList(Answer answer, Integer page) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return commentRepository.findAllOfAnswer(answer, pageable);
    }

    public CommentsVO getCommentsOfAnswer(Answer answer, Integer pageIdx, String username) {
        Page<Comment> commentPage = getList(answer, pageIdx);

        CommentsVO commentsVO = new CommentsVO();
        commentsVO.injectDatum(commentPage, username);

        return commentsVO;
    }

    public CommentsVO getCommentsOfAnswer(Answer answer, Integer pageIdx) {
        Page<Comment> commentPage = getList(answer, pageIdx);

        CommentsVO commentsVO = new CommentsVO();
        commentsVO.injectDatum(commentPage);

        return commentsVO;
    }

    public CommentsVO getCommentsOfQuestion(Question question, Integer pageIdx) {
        Page<Comment> commentPage = getList(question, pageIdx);

        CommentsVO commentsVO = new CommentsVO();
        commentsVO.injectDatum(commentPage);

        return commentsVO;
    }

    public CommentsVO getCommentsOfQuestion(Question question, Integer pageIdx, String username) {
        Page<Comment> commentPage = getList(question, pageIdx);

        CommentsVO commentsVO = new CommentsVO();
        commentsVO.injectDatum(commentPage, username);

        return commentsVO;
    }
}
