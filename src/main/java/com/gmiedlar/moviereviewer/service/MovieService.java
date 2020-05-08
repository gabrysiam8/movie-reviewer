package com.gmiedlar.moviereviewer.service;

import java.util.List;

import com.gmiedlar.moviereviewer.domain.Comment;
import com.gmiedlar.moviereviewer.domain.Movie;

public interface MovieService {
    Movie addMovie(String username, Movie movie);
    List<Movie> getAllMovies();
    Movie getMovieById(String id);
    Movie updateMovie(String id, Movie movieUpdate);
    String deleteMovie(String id);
    List<Comment> getMovieComments(String id);
    Movie addMovieComment(String username, String id, Comment comment);
    Movie deleteMovieComment(String movieId, String commentId);
}
