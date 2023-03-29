package com.mysite.sbb.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final AnswerService answerService;
    private final QuestionService questionService;
    private final UserService userService;
    private final CommentService commentService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/question/{id}")
    public String addQuestionComment(@Valid CommentForm commentForm, BindingResult bindingResult,
                             Principal principal, @PathVariable("id") Integer id) {
        SiteUser user = userService.getUser(principal.getName());
        Question question = questionService.getQuestion(id);

        if (!bindingResult.hasErrors()) {
            commentService.create(question, commentForm.getContent(), user);
        }
        return "redirect:/question/detail/" + id;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/answer/{id}")
    public String addAnswerComment(@Valid CommentForm commentForm, BindingResult bindingResult,
                             Principal principal, @PathVariable("id") Integer id) {
        SiteUser user = userService.getUser(principal.getName());
        Answer answer = answerService.getAnswer(id);

        if (!bindingResult.hasErrors()) {
            commentService.create(answer, commentForm.getContent(), user);
        }
        return "redirect:/question/detail/" + answer.getQuestion().getId();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String deleteComment(Principal principal, @PathVariable("id") Integer id, HttpServletRequest request) {
        Comment comment = commentService.getComment(id);
        if (!principal.getName().equals(comment.getAuthor().getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        commentService.delete(comment);
        return "redirect:" + request.getHeader("Referer");
    }

    @GetMapping("/recent")
    public String recentComment(@RequestParam(name = "page", defaultValue = "0") Integer page, Model model){
        Page<Comment> recentComments = commentService.getList(page);
        model.addAttribute("commentPage", recentComments);

        return "recent_comment";
    }

    @RequestMapping("/ajax/pagination-of-question")
    @ResponseBody
    public CommentsVO getListOfQuestion(@RequestBody PaginationVO pageObject, Authentication authentication) {
        Question question = questionService.getQuestion(pageObject.questionId);
        Page<Comment> commentPage = commentService.getList(question, pageObject.pageIdx);

        CommentsVO commentsVO = new CommentsVO();
        commentsVO.totalPages = commentPage.getTotalPages();
        commentsVO.number = commentPage.getNumber();
        commentPage
                .map(comment -> new CommentVO(
                        comment.getId(),
                        comment.getContent(),
                        comment.getCreateDate(),
                        comment.getAuthor().getName(),
                        authentication != null
                                && comment.getAuthor().getName().equals(authentication.getName())))
                .forEach(vo->commentsVO.content.add(vo));

        return commentsVO;
    }

    @RequestMapping("/ajax/pagination-of-answer")
    @ResponseBody
    public CommentsVO getListOfAnswer(@RequestBody PaginationVO pageObject, Authentication authentication) {
        //TODO
        Answer answer = answerService.getAnswer(pageObject.answerId);
        Page<Comment> commentPage = commentService.getList(answer, pageObject.pageIdx);

        CommentsVO commentsVO = new CommentsVO();
        commentsVO.totalPages = commentPage.getTotalPages();
        commentsVO.number = commentPage.getNumber();
        commentPage
                .map(comment -> new CommentVO(
                        comment.getId(),
                        comment.getContent(),
                        comment.getCreateDate(),
                        comment.getAuthor().getName(),
                        authentication != null
                                && comment.getAuthor().getName().equals(authentication.getName())))
                .forEach(vo -> commentsVO.content.add(vo));

        return commentsVO;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    static class PaginationVO{
        Integer questionId;
        Integer answerId;
        Integer pageIdx;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class CommentsVO {
        Integer totalPages;
        Integer number;
        ArrayList<CommentVO> content = new ArrayList<>();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class CommentVO {
        Integer commentId;
        String content;

        /**
         * 잭슨에서 LocalDateTime 직렬화에 문제가 있다캄
         * 이거 없으면 직렬화 안됨
         */
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
        LocalDateTime createDate;
        String author;
        Boolean isAuthor;
    }
}














