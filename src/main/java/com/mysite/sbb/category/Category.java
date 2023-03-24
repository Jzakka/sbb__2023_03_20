package com.mysite.sbb.category;

import com.mysite.sbb.question.Question;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of={"id", "name", "createDate"})
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String name;

    private LocalDateTime createDate;

    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    private List<Question> questions = new ArrayList<>();
}
