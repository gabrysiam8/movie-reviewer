package com.gmiedlar.moviereviewer.service;

import java.util.List;

import com.gmiedlar.moviereviewer.domain.Movie;

public interface MovieService {
    Movie addMovie(String username, Movie movie);
    List<Movie> getAllMovies();
    List<Movie> getAllUserMovies(String username);
    Movie getMovieById(String id);
    Movie updateMovie(String id, Movie movieUpdate);
    String deleteMovie(String id);
}
