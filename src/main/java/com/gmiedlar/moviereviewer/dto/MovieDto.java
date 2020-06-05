package com.gmiedlar.moviereviewer.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDto {

    private String id;

    private String title;

    private String genre;

    private Integer year;

    private String director;

    private List<String> commentIds;

    private double avgRating;

    private boolean canComment;
}
