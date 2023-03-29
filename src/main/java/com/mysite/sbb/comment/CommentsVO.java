package com.mysite.sbb.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CommentsVO {
    Integer totalPages;
    Integer number;
    ArrayList<CommentVO> content = new ArrayList<>();

    public void injectDatum(Page<Comment> commentPage) {
        this.totalPages = commentPage.getTotalPages();
        this.number = commentPage.getNumber();
        commentPage
                .map(comment -> new CommentsVO.CommentVO(
                        comment.getId(),
                        comment.getContent(),
                        comment.getCreateDate(),
                        comment.getAuthor().getName(),
                        false))
                .forEach(vo -> this.content.add(vo));
    }

    public void injectDatum(Page<Comment> commentPage, String username) {
        this.totalPages = commentPage.getTotalPages();
        this.number = commentPage.getNumber();
        commentPage
                .map(comment -> new CommentsVO.CommentVO(
                        comment.getId(),
                        comment.getContent(),
                        comment.getCreateDate(),
                        comment.getAuthor().getName(),
                        comment.getAuthor().getName().equals(username)))
                .forEach(vo -> this.content.add(vo));
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
