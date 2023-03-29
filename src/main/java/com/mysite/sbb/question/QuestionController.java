package com.mysite.sbb.question;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.category.Category;
import com.mysite.sbb.category.CategoryService;
import com.mysite.sbb.comment.Comment;
import com.mysite.sbb.comment.CommentController;
import com.mysite.sbb.comment.CommentForm;
import com.mysite.sbb.comment.CommentService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;
    private final CategoryService categoryService;
    private final CommentService commentService;

    @ModelAttribute("categories")
    public List<Category> categories() {
        return categoryService.getList();
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "category") Optional<String> category) {
        Optional<Category> categoryOptional = categoryService.get(category);
        Page<Question> paging = questionService.getList(page, kw, categoryOptional);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        categoryOptional.ifPresent(value -> model.addAttribute("category", value.getName()));

        return "question_list";
    }

    //TODO 리팩필요
    @GetMapping("/detail/{id}")
    public String detail(Principal principal, Model model, @PathVariable("id") Integer id, @ModelAttribute CommentForm commentForm,
                         AnswerForm answerForm, @RequestParam(name = "page", defaultValue = "0") int page) {
        Question question = questionService.getQuestion(id);
        questionService.increaseView(question);
        Page<Answer> answerPage = answerService.getList(question, page);
        Page<Comment> questionComments = commentService.getList(question, 0);

        AnswerPageDTO answerPageDTO = AnswerPageDTO
                .builder()
                .totalPages(answerPage.getTotalPages())
                .number(answerPage.getNumber())
                .hasNext(answerPage.hasNext())
                .hasPrevious(answerPage.hasPrevious())
                .isEmpty(answerPage.getTotalPages()==0)
                .answers(new ArrayList<>())
                .build();

        answerPage.forEach(answer -> {
            Page<Comment> commentPage = commentService.getList(answer, 0);
            AnswerDTO answerDTO = AnswerDTO.builder()
                    .id(answer.getId())
                    .author(answer.getAuthor().getName())
                    .content(answer.getContent())
                    .createDate(answer.getCreateDate())
                    .voter(answer.getVoter().size())
                    .modifiedDate(answer.getModifiedDate()).build();

            ArrayList<CommentController.CommentVO> commentVOS = new ArrayList<>();
            commentPage.forEach(comment -> commentVOS.add(new CommentController.CommentVO(
                    comment.getId()
                    ,comment.getContent()
                    ,comment.getCreateDate()
                    ,comment.getAuthor().getName()
                    ,principal != null && principal.getName().equals(comment.getAuthor().getName())
            )));
            answerDTO.commentsVO = new CommentController.CommentsVO(commentPage.getTotalPages(), commentPage.getNumber(), commentVOS);
            answerPageDTO.getAnswers().add(answerDTO);
        });

        model.addAttribute("question", question);
        model.addAttribute("commentPage", questionComments);
        model.addAttribute("answerPage", answerPageDTO);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm, Model model) {
        List<Category> categories = categoryService.getList();
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        SiteUser user = userService.getUser(principal.getName());
        Optional<Category> category = categoryService.get(questionForm.getCategory());
        questionService.create(questionForm.getSubject(), category, questionForm.getContent(), user);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = questionService.getQuestion(id);
        if (!question.getAuthor().getName().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 @PathVariable("id") Integer id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = questionService.getQuestion(id);
        if (!question.getAuthor().getName().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 업습니다.");
        }
        questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = questionService.getQuestion(id);
        if (!question.getAuthor().getName().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Question question = questionService.getQuestion(id);
        SiteUser user = userService.getUser(principal.getName());
        questionService.vote(question, user);
        return String.format("redirect:/question/detail/%s", id);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class AnswerPageDTO {
        Integer number;
        Integer totalPages;
        Boolean hasNext;
        Boolean hasPrevious;
        Boolean isEmpty;
        ArrayList<AnswerDTO> answers = new ArrayList<>();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    private static class AnswerDTO {
        Integer id;
        String author;
        String content;
        Integer voter;
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
        LocalDateTime createDate;
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd hh:mm")
        LocalDateTime modifiedDate;
        CommentController.CommentsVO commentsVO;
    }
}
