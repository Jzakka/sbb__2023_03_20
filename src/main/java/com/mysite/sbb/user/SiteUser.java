package com.mysite.sbb.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
}
