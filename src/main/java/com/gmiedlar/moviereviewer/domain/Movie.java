package com.gmiedlar.moviereviewer.domain;

import java.util.List;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="movie")
@Data
public class Movie {

    @Id
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String genre;

    private int year;

    @NotBlank
    private String director;

    private List<@NotBlank String> commentIds;

    private double avgRating;

    private String userId;
}
