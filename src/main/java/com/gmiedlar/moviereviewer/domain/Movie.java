package com.gmiedlar.moviereviewer.domain;

import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.sun.jdi.IntegerValue;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="movie")
@Data
@Builder
public class Movie {

    @Id
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String genre;

    @NotNull
    private Integer year;

    @NotBlank
    private String director;

    private List<@NotBlank String> commentIds;

    private double avgRating;

    private String userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Movie movie = (Movie) o;
        return year.equals(movie.year) &&
            Double.compare(movie.avgRating, avgRating) == 0 &&
            Objects.equals(id, movie.id) &&
            Objects.equals(title, movie.title) &&
            Objects.equals(genre, movie.genre) &&
            Objects.equals(director, movie.director) &&
            Objects.equals(commentIds, movie.commentIds) &&
            Objects.equals(userId, movie.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, genre, year, director, commentIds, avgRating, userId);
    }
}
