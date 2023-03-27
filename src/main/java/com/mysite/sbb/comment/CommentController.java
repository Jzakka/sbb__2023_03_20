package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

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

    @GetMapping("/recent")
    public String recentComment(@RequestParam(name = "page", defaultValue = "0") Integer page, Model model){
        Page<Comment> recentComments = commentService.getList(page);
        model.addAttribute("commentPage", recentComments);

        return "recent_comment";
    }
}
