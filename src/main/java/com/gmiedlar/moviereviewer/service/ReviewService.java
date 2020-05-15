package com.gmiedlar.moviereviewer.service;

import java.util.List;

import com.gmiedlar.moviereviewer.domain.Comment;
import com.gmiedlar.moviereviewer.domain.Movie;

public interface ReviewService {
    List<Comment> getMovieComments(String id);
    Movie addMovieComment(String username, String id, Comment comment);
    Comment updateMovieComment(String username, String id, Comment comment);
    Movie deleteMovieComment(String movieId, String commentId);
}
