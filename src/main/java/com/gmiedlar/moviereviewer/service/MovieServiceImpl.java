package com.gmiedlar.moviereviewer.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.gmiedlar.moviereviewer.domain.Comment;
import com.gmiedlar.moviereviewer.domain.CustomUser;
import com.gmiedlar.moviereviewer.domain.Movie;
import com.gmiedlar.moviereviewer.dto.MovieDto;
import com.gmiedlar.moviereviewer.repository.MovieRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class MovieServiceImpl implements MovieService, ReviewService {

    private final MovieRepository repository;

    private final CommentService commentService;

    private final UserFinderService userFinderService;

    private final ModelMapper mapper;

    public MovieServiceImpl(MovieRepository repository, CommentService commentService, UserFinderService userFinderService, ModelMapper mapper) {
        this.repository = repository;
        this.commentService = commentService;
        this.userFinderService = userFinderService;
        this.mapper = mapper;
    }

    @Override
    public Movie addMovie(String username, Movie movie) {
        CustomUser currentUser = userFinderService.findUserByUsername(username);
        movie.setUserId(currentUser.getId());
        movie.setCommentIds(Collections.emptyList());
        return repository.save(movie);
    }

    @Override
    public List<Movie> getAllMovies() {
        return repository.findAll();
    }

    @Override
    public List<Movie> getAllUserMovies(String username) {
        CustomUser currentUser = userFinderService.findUserByUsername(username);
        return repository.findByUserId(currentUser.getId());
    }

    private Movie getMovieById(String id) {
        return repository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No movie with that id exists!"));
    }

    @Override
    public MovieDto getMovieDetails(String username, String id) {
        Movie movie = getMovieById(id);
        MovieDto movieDto = mapper.map(movie, MovieDto.class);

        Optional.ofNullable(username)
                .ifPresent(u -> {
                    CustomUser currentUser = userFinderService.findUserByUsername(u);

                    boolean canComment = movie.getCommentIds().stream()
                                              .map(commentService::getCommentById)
                                              .noneMatch(comment -> comment.getAuthorId().equals(currentUser.getId()));

                    movieDto.setCanComment(canComment);
                });

        return movieDto;
    }

    @Override
    public Movie updateMovie(String id, Movie movieUpdate) {
        Movie movie = getMovieById(id);

        movieUpdate.setId(id);
        movieUpdate.setCommentIds(movie.getCommentIds());
        movieUpdate.setAvgRating(movie.getAvgRating());
        movieUpdate.setUserId(movie.getUserId());
        if(movieUpdate.equals(movie))
            return movie;

        return repository.save(movieUpdate);
    }

    @Override
    public String deleteMovie(String id) {
        if(!repository.existsById(id))
            throw new IllegalArgumentException("No movie with that id exists!");

        deleteMovieComments(id);
        repository.deleteById(id);
        return "Movie successfully deleted";
    }

    @Override
    public List<Comment> getMovieComments(String id) {
        return getAllMovieComments(id);
    }

    @Override
    public Movie addMovieComment(String username, String id, Comment comment) {
        Comment newComment = commentService.addComment(username, comment);

        Movie movieUpdate = getMovieById(id);
        movieUpdate.getCommentIds().add(newComment.getId());
        double avgRatingUpdate = calculateAvgRating(movieUpdate);
        movieUpdate.setAvgRating(avgRatingUpdate);

        repository.save(movieUpdate);
        return movieUpdate;
    }

    @Override
    public Comment updateMovieComment(String id, String commentId, Comment commentUpdate) {
        if(!repository.existsById(id))
            throw new IllegalArgumentException("No movie with that id exists!");

        Comment updatedComment = commentService.updateComment(commentId, commentUpdate);
        Movie movieUpdate = getMovieById(id);
        double avgRatingUpdate = calculateAvgRating(movieUpdate);
        movieUpdate.setAvgRating(avgRatingUpdate);

        repository.save(movieUpdate);
        return updatedComment;
    }

    @Override
    public Movie deleteMovieComment(String movieId, String commentId) {
        if(!repository.existsById(movieId))
            throw new IllegalArgumentException("No movie with that id exists!");

        commentService.deleteComment(commentId);
        Movie movieUpdate = getMovieById(movieId);
        movieUpdate.getCommentIds().remove(commentId);
        double avgRatingUpdate = calculateAvgRating(movieUpdate);
        movieUpdate.setAvgRating(avgRatingUpdate);

        repository.save(movieUpdate);
        return movieUpdate;
    }

    private void deleteMovieComments(String id) {
        getMovieById(id)
            .getCommentIds()
            .forEach(commentService::deleteComment);
    }

    private List<Comment> getAllMovieComments(String id) {
        return getMovieById(id)
            .getCommentIds()
            .stream()
            .map(commentService::getCommentById)
            .collect(Collectors.toList());

    }

    private double calculateAvgRating(Movie movie) {
        return movie.getCommentIds().stream()
                    .map(commentService::getCommentById)
                    .map(Comment::getRating)
                    .mapToDouble(Integer::intValue)
                    .average()
                    .orElse(0.0);
    }
}