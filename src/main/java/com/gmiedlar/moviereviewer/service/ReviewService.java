package com.gmiedlar.moviereviewer.service;

import java.util.List;

import com.gmiedlar.moviereviewer.domain.Comment;
import com.gmiedlar.moviereviewer.domain.Movie;

public interface ReviewService {
    List<Comment> getMovieComments(String id);
    Movie addMovieComment(String username, String id, Comment comment);
    Comment updateMovieComment(String id, String commentId, Comment comment);
    Movie deleteMovieComment(String movieId, String commentId);
}
