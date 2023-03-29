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
