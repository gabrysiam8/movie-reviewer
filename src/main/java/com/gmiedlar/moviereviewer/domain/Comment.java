package com.gmiedlar.moviereviewer.domain;

import java.util.Date;
import java.util.Objects;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="comment")
@Data
@Builder
public class Comment {
    @Id
    private String id;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer rating;

    private String text;

    private String authorId;

    private Date addDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id) &&
            Objects.equals(rating, comment.rating) &&
            Objects.equals(text, comment.text) &&
            Objects.equals(authorId, comment.authorId) &&
            Objects.equals(addDate, comment.addDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rating, text, authorId, addDate);
    }
}
