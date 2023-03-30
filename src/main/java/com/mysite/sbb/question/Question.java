package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.category.Category;
import com.mysite.sbb.comment.Comment;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer views = 0;

    private LocalDateTime createDate;

    private LocalDateTime modifiedDate;

    @ManyToOne
    private SiteUser author;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<Answer> answerList = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    List<Comment> comments = new ArrayList<>();

    @ManyToMany
    Set<SiteUser> voter;

    @ManyToOne
    Category category;


    public void addAnswer(Answer answer) {
        answer.setQuestion(this);
        answerList.add(answer);
    }

    public void addComment(Comment comment) {
        comment.setQuestion(this);
        comments.add(comment);
    }

    public void increaseView() {
        views++;
    }
}
