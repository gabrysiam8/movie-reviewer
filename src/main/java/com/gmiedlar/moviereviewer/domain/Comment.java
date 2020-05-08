package com.gmiedlar.moviereviewer.domain;

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
}
