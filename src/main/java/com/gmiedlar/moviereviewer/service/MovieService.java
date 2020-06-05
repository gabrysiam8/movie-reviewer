package com.gmiedlar.moviereviewer.service;

import java.util.List;

import com.gmiedlar.moviereviewer.domain.Movie;
import com.gmiedlar.moviereviewer.dto.MovieDto;

public interface MovieService {
    Movie addMovie(String username, Movie movie);
    List<Movie> getAllMovies();
    List<Movie> getAllUserMovies(String username);
    MovieDto getMovieDetails(String username, String id);
    Movie updateMovie(String id, Movie movieUpdate);
    String deleteMovie(String id);
}
