package com.gmiedlar.moviereviewer.domain;

import java.util.Objects;

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

    private double rating;

    private String text;

    private String authorId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Comment comment = (Comment) o;
        return Double.compare(comment.rating, rating) == 0 &&
            Objects.equals(id, comment.id) &&
            Objects.equals(text, comment.text) &&
            Objects.equals(authorId, comment.authorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rating, text, authorId);
    }
}
