package com.gmiedlar.moviereviewer.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.gmiedlar.moviereviewer.domain.Comment;
import com.gmiedlar.moviereviewer.domain.Movie;
import com.gmiedlar.moviereviewer.dto.MovieDto;
import com.gmiedlar.moviereviewer.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.gmiedlar.moviereviewer.common.TestData.COMMENT;
import static com.gmiedlar.moviereviewer.common.TestData.COMMENT_ID;
import static com.gmiedlar.moviereviewer.common.TestData.ENABLED_USER;
import static com.gmiedlar.moviereviewer.common.TestData.MOVIE;
import static com.gmiedlar.moviereviewer.common.TestData.MOVIE_DTO;
import static com.gmiedlar.moviereviewer.common.TestData.MOVIE_ID;
import static com.gmiedlar.moviereviewer.common.TestData.UNIQUE_USERNAME;
import static com.gmiedlar.moviereviewer.common.TestData.USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository repository;

    @Mock
    private CommentService commentService;

    @Mock
    private UserFinderService userFinderService;

    private ModelMapper mapper;

    private MovieServiceImpl movieService;

    private Movie movie;

    private Movie movieWithComments;

    @BeforeEach
    void setUp() {
        movieService = new MovieServiceImpl(repository, commentService, userFinderService, mapper);

        movie = Movie.builder()
                     .title("Test title")
                     .genre("test genre")
                     .year(2020)
                     .director("Test director")
                     .build();

        movieWithComments = Movie.builder()
                                 .id(MOVIE_ID)
                                 .title("Test title")
                                 .genre("test genre")
                                 .year(2020)
                                 .director("Test director")
                                 .commentIds(new ArrayList<>(Collections.singletonList(COMMENT_ID)))
                                 .avgRating(5)
                                 .userId(USER_ID)
                                 .build();
    }

    @Test
    public void shouldAddMovie() {
        //given
        given(userFinderService.findUserByUsername(anyString())).willReturn(ENABLED_USER);
        given(repository.save(any(Movie.class))).willReturn(MOVIE);

        //when
        Movie result = movieService.addMovie(UNIQUE_USERNAME, movie);

        //then
        verify(repository, times(1)).save(any(Movie.class));
        assertNotNull(result);
        assertEquals(MOVIE, result);
    }

    @Test
    public void shouldNotAddMovieWhenUsernameNotFoundException() {
        //given
        given(userFinderService.findUserByUsername(anyString())).willThrow(UsernameNotFoundException.class);

        assertThrows(
            UsernameNotFoundException.class,
            //when
            () -> movieService.addMovie(UNIQUE_USERNAME, movie)
        );

        //then
        verify(repository, never()).save(any(Movie.class));
    }

    @Test
    public void shouldGetAllMovies() {
        //given
        given(repository.findAll()).willReturn(List.of(MOVIE));

        //when
        List<Movie> movies = movieService.getAllMovies();

        //then
        verify(repository, times(1)).findAll();
        assertEquals(1, movies.size());
        assertEquals(MOVIE, movies.get(0));
    }

    @Test
    public void shouldGetAllUserMovies() {
        //given
        given(userFinderService.findUserByUsername(anyString())).willReturn(ENABLED_USER);
        given(repository.findByUserId(anyString())).willReturn(List.of(MOVIE));

        //when
        List<Movie> movies = movieService.getAllUserMovies(UNIQUE_USERNAME);

        //then
        verify(repository, times(1)).findByUserId(anyString());
        assertEquals(1, movies.size());
        assertEquals(MOVIE, movies.get(0));
    }

    @Test
    public void shouldGetMovieDetails() {
        //given
        given(repository.findById(MOVIE_ID)).willReturn(Optional.ofNullable(MOVIE));

        //when
        MovieDto result = movieService.getMovieDetails(null, MOVIE_ID);

        //then
        verify(repository, times(1)).findById(anyString());
        assertNotNull(result);
        assertEquals(MOVIE_DTO, result);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenInvalidMovieId() {
        //given
        String invalidMovieId = "invalidId-1234";
        given(repository.findById(invalidMovieId)).willReturn(Optional.empty());

        //when
        Throwable exception = assertThrows(
            IllegalArgumentException.class,
            //when
            () -> movieService.getMovieDetails(null, invalidMovieId)
        );

        //then
        verify(repository, times(1)).findById(anyString());
        assertEquals("No movie with that id exists!", exception.getMessage());
    }

    @Test
    public void shouldUpdateMovie() {
        //given
        movie.setTitle("Updated title");
        Movie movieUpdate = Movie.builder()
                                 .id(MOVIE_ID)
                                 .title("Updated title")
                                 .genre("test genre")
                                 .year(2020)
                                 .director("Test director")
                                 .commentIds(Collections.emptyList())
                                 .userId(USER_ID)
                                 .build();

        given(repository.findById(MOVIE_ID)).willReturn(Optional.ofNullable(MOVIE));
        given(repository.save(any(Movie.class))).willReturn(movieUpdate);

        //when
        Movie result = movieService.updateMovie(MOVIE_ID, movie);

        //then
        verify(repository, times(1)).findById(anyString());
        verify(repository, times(1)).save(any(Movie.class));
        assertNotNull(result);
        assertEquals(movieUpdate, result);
    }

    @Test
    public void shouldDeleteMovieWithComments() {
        //given
        given(repository.existsById(MOVIE_ID)).willReturn(true);
        given(repository.findById(MOVIE_ID)).willReturn(Optional.of(movieWithComments));

        //when
        String result = movieService.deleteMovie(MOVIE_ID);

        //then
        verify(repository, times(1)).existsById(anyString());
        verify(repository, times(1)).deleteById(anyString());
        verify(commentService, times(1)).deleteComment(anyString());
        assertNotNull(result);
        assertEquals("Movie successfully deleted", result);
    }

    @Test
    public void shouldGetAllMovieComments() {
        //given
        given(repository.findById(MOVIE_ID)).willReturn(Optional.of(movieWithComments));
        given(commentService.getCommentById(COMMENT_ID)).willReturn(COMMENT);

        //when
        List<Comment> movieComments = movieService.getMovieComments(MOVIE_ID);

        //then
        verify(repository, times(1)).findById(anyString());
        verify(commentService, times(1)).getCommentById(anyString());
        assertEquals(1, movieComments.size());
        assertEquals(COMMENT, movieComments.get(0));
    }

    @Test
    public void shouldNotGetMovieCommentsWhenIllegalArgumentException() {
        //given
        given(repository.findById(MOVIE_ID)).willReturn(Optional.of(movieWithComments));
        given(commentService.getCommentById(COMMENT_ID)).willThrow(IllegalArgumentException.class);

        assertThrows(
            IllegalArgumentException.class,
            //when
            () -> movieService.getMovieComments(MOVIE_ID)
        );

        //then
        verify(repository, times(1)).findById(anyString());
        verify(commentService, times(1)).getCommentById(anyString());
    }

    @Test
    public void shouldAddCommentToMovie() {
        //given
        Comment comment = Comment.builder()
                                 .rating(5)
                                 .text("Awesome")
                                 .build();

        given(commentService.addComment(UNIQUE_USERNAME, comment)).willReturn(COMMENT);
        given(repository.findById(MOVIE_ID)).willReturn(Optional.of(movieWithComments));
        given(commentService.getCommentById(COMMENT_ID)).willReturn(COMMENT);

        //when
        Movie result = movieService.addMovieComment(UNIQUE_USERNAME, MOVIE_ID, comment);

        //then
        verify(commentService, times(1)).addComment(anyString(), any(Comment.class));
        verify(repository, times(1)).findById(anyString());
        verify(commentService, times(2)).getCommentById(anyString());
        assertEquals(2, result.getCommentIds().size());
        assertEquals(5, result.getAvgRating());
    }

    @Test
    public void shouldNotAddMovieCommentWhenUsernameNotFoundException() {
        //given
        Comment comment = Comment.builder()
                                 .rating(5)
                                 .text("Awesome")
                                 .build();
        given(commentService.addComment(UNIQUE_USERNAME, comment)).willThrow(UsernameNotFoundException.class);

        assertThrows(
            UsernameNotFoundException.class,
            //when
            () -> movieService.addMovieComment(UNIQUE_USERNAME, MOVIE_ID, comment)
        );

        //then
        verify(commentService, times(1)).addComment(anyString(), any(Comment.class));
        verify(repository, never()).findById(anyString());
        verify(commentService, never()).getCommentById(anyString());
    }

    @Test
    public void shouldUpdateMovieComment() {
        //given
        Comment comment = Comment.builder()
                                 .id(COMMENT_ID)
                                 .rating(4)
                                 .text("Awesome")
                                 .build();

        given(repository.existsById(MOVIE_ID)).willReturn(true);
        given(commentService.updateComment(anyString(), any(Comment.class))).willReturn(comment);
        given(repository.findById(MOVIE_ID)).willReturn(Optional.of(movieWithComments));
        given(commentService.getCommentById(COMMENT_ID)).willReturn(comment);
        given(repository.save(any(Movie.class))).willReturn(movieWithComments);

        //when
        Comment result = movieService.updateMovieComment(MOVIE_ID, COMMENT_ID, comment);

        //then
        verify(repository, times(1)).existsById(anyString());
        verify(commentService, times(1)).updateComment(anyString(), any(Comment.class));
        verify(repository, times(1)).findById(anyString());
        verify(commentService, times(1)).getCommentById(anyString());
        verify(repository, times(1)).save(any(Movie.class));
        assertEquals(comment.getRating(), result.getRating());
        assertEquals(comment.getText(), result.getText());
    }

    @Test
    public void shouldDeleteMovieComment() {
        //given
        given(repository.existsById(MOVIE_ID)).willReturn(true);
        given(commentService.deleteComment(COMMENT_ID)).willReturn("Comment successfully deleted");
        given(repository.findById(MOVIE_ID)).willReturn(Optional.of(movieWithComments));

        //when
        Movie result = movieService.deleteMovieComment(MOVIE_ID, COMMENT_ID);

        //then
        verify(repository, times(1)).existsById(anyString());
        verify(commentService, times(1)).deleteComment(anyString());
        verify(repository, times(1)).findById(anyString());
        assertEquals(0, result.getCommentIds().size());
        assertEquals(0.0, result.getAvgRating());
    }

    @Test
    public void shouldNotDeleteMovieCommentWhenCommentNotFound() {
        //given
        given(repository.existsById(MOVIE_ID)).willReturn(true);
        given(commentService.deleteComment(COMMENT_ID)).willThrow(IllegalArgumentException.class);

        assertThrows(
            IllegalArgumentException.class,
            //when
            () -> movieService.deleteMovieComment(MOVIE_ID, COMMENT_ID)
        );

        //then
        verify(repository, times(1)).existsById(anyString());
        verify(commentService, times(1)).deleteComment(anyString());
        verify(repository, never()).findById(anyString());
    }
}