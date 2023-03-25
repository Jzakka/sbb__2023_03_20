package com.mysite.sbb.user;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.comment.Comment;
import com.mysite.sbb.question.Question;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class SiteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String password;

    @Column(unique = true)
    private String email;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

    @OneToMany(mappedBy =  "author", cascade = CascadeType.REMOVE)
    List<Answer> answers = new ArrayList<>();

    @OneToMany(mappedBy =  "author", cascade = CascadeType.REMOVE)
    List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy =  "author", cascade = CascadeType.REMOVE)
    List<Comment> comments = new ArrayList<>();
}
