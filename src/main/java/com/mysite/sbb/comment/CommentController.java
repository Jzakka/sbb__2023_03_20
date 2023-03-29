package com.mysite.sbb.comment;

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
    public String recentComment(@RequestParam(name = "page", defaultValue = "0") Integer page, Model model) {
        Page<Comment> recentComments = commentService.getList(page);
        model.addAttribute("commentPage", recentComments);

        return "recent_comment";
    }

    @RequestMapping("/ajax/pagination-of-question")
    @ResponseBody
    public CommentsVO getListOfQuestion(@RequestBody PaginationVO pageObject, Authentication authentication) {
        Question question = questionService.getQuestion(pageObject.questionId);
        if (authentication != null) {
            return commentService.getCommentsOfQuestion(question, pageObject.pageIdx, authentication.getName());
        }
        return commentService.getCommentsOfQuestion(question, pageObject.pageIdx);

    }

    @RequestMapping("/ajax/pagination-of-answer")
    @ResponseBody
    public CommentsVO getListOfAnswer(@RequestBody PaginationVO pageObject, Authentication authentication) {
        Answer answer = answerService.getAnswer(pageObject.answerId);
        if (authentication != null) {
            return commentService.getCommentsOfAnswer(answer, pageObject.pageIdx, authentication.getName());
        }
        return commentService.getCommentsOfAnswer(answer, pageObject.pageIdx);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    static class PaginationVO {
        Integer questionId;
        Integer answerId;
        Integer pageIdx;
    }
}














