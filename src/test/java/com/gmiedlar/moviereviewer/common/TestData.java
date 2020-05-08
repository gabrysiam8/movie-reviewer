package com.gmiedlar.moviereviewer.common;

import java.util.Collections;

import com.gmiedlar.moviereviewer.domain.Comment;
import com.gmiedlar.moviereviewer.domain.CustomUser;
import com.gmiedlar.moviereviewer.domain.Movie;

public class TestData {
    public static final String USER_ID = "userId-1234";

    public static final String UNIQUE_USERNAME = "test";

    public static final String UNIQUE_EMAIL = "test@gmail.com";

    public static final String CONFIRMATION_TOKEN = "token-1234";

    public static CustomUser DISABLED_USER = CustomUser.builder()
                                                       .id(USER_ID)
                                                       .email(UNIQUE_EMAIL)
                                                       .username(UNIQUE_USERNAME)
                                                       .password("pass")
                                                       .role("USER")
                                                       .build();

    public static CustomUser ENABLED_USER = CustomUser.builder()
                                                      .id(USER_ID)
                                                      .email(UNIQUE_EMAIL)
                                                      .username(UNIQUE_USERNAME)
                                                      .password("pass")
                                                      .role("USER")
                                                      .enabled(true)
                                                      .build();

    public static final String COMMENT_ID = "commentId-1234";

    public static final Comment COMMENT = Comment.builder()
                                                 .id(COMMENT_ID)
                                                 .rating(5)
                                                 .text("Awesome")
                                                 .authorId(USER_ID)
                                                 .build();

    public static final String MOVIE_ID = "movieId-1234";

    public static final Movie MOVIE = Movie.builder()
                                           .id(MOVIE_ID)
                                           .title("Test title")
                                           .genre("test genre")
                                           .year(2020)
                                           .director("Test director")
                                           .commentIds(Collections.emptyList())
                                           .userId(USER_ID)
                                           .build();
}
