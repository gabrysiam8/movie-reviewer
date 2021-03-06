package com.gmiedlar.moviereviewer.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gmiedlar.moviereviewer.domain.Comment;
import com.gmiedlar.moviereviewer.domain.Movie;
import com.gmiedlar.moviereviewer.dto.MovieDto;
import com.gmiedlar.moviereviewer.service.MovieService;
import com.gmiedlar.moviereviewer.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.gmiedlar.moviereviewer.common.TestData.COMMENT;
import static com.gmiedlar.moviereviewer.common.TestData.COMMENT_ID;
import static com.gmiedlar.moviereviewer.common.TestData.MOVIE;
import static com.gmiedlar.moviereviewer.common.TestData.MOVIE_DTO;
import static com.gmiedlar.moviereviewer.common.TestData.MOVIE_ID;
import static com.gmiedlar.moviereviewer.common.TestData.UNIQUE_USERNAME;
import static com.gmiedlar.moviereviewer.common.TestData.USER_ID;
import static com.gmiedlar.moviereviewer.common.TestUtils.readFile;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService service;

    @MockBean
    private ReviewService reviewService;

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                     .id(MOVIE_ID)
                     .title("Test title")
                     .genre("test genre")
                     .year(2020)
                     .director("Test director")
                     .commentIds(new ArrayList<>(Collections.singletonList(COMMENT_ID)))
                     .userId(USER_ID)
                     .build();
    }

    @Test
    @WithMockUser(username = UNIQUE_USERNAME)
    public void shouldReturnNewMovieWhenSuccessfullyAdded() throws Exception {
        given(service.addMovie(anyString(), any(Movie.class))).willReturn(MOVIE);

        mockMvc.perform(post("/movie")
            .content(readFile("requests/movie.json"))
            .contentType(APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(MOVIE_ID))
               .andExpect(jsonPath("$.title").value("Test title"))
               .andExpect(jsonPath("$.genre").value("test genre"))
               .andExpect(jsonPath("$.year").value(2020))
               .andExpect(jsonPath("$.director").value("Test director"));
    }

    @Test
    @WithMockUser(username = UNIQUE_USERNAME)
    public void shouldReturnBadRequestWhenInvalidMovieContent() throws Exception {
        mockMvc.perform(post("/movie")
            .content("{}")
            .contentType(APPLICATION_JSON))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnAllMovies() throws Exception {
        given(service.getAllMovies()).willReturn(List.of(MOVIE));

        mockMvc.perform(get("/movie/all"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(1))
               .andExpect(jsonPath("$[0].id").value(MOVIE_ID))
               .andExpect(jsonPath("$[0].title").value("Test title"))
               .andExpect(jsonPath("$[0].genre").value("test genre"))
               .andExpect(jsonPath("$[0].year").value(2020))
               .andExpect(jsonPath("$[0].director").value("Test director"))
               .andExpect(jsonPath("$[0].userId").value(USER_ID));
    }

    @Test
    @WithMockUser(username = UNIQUE_USERNAME)
    public void shouldReturnAllUserMovies() throws Exception {
        given(service.getAllUserMovies(UNIQUE_USERNAME)).willReturn(List.of(MOVIE));

        mockMvc.perform(get("/movie"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(1))
               .andExpect(jsonPath("$[0].id").value(MOVIE_ID))
               .andExpect(jsonPath("$[0].title").value("Test title"))
               .andExpect(jsonPath("$[0].genre").value("test genre"))
               .andExpect(jsonPath("$[0].year").value(2020))
               .andExpect(jsonPath("$[0].director").value("Test director"))
               .andExpect(jsonPath("$[0].userId").value(USER_ID));
    }

    @Test
    public void shouldReturnMovieWhenIdExist() throws Exception {
        given(service.getMovieDetails(null, MOVIE_ID)).willReturn(MOVIE_DTO);

        mockMvc.perform(get("/movie/" + MOVIE_ID))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(MOVIE_ID))
               .andExpect(jsonPath("$.title").value("Test title"))
               .andExpect(jsonPath("$.genre").value("test genre"))
               .andExpect(jsonPath("$.year").value(2020))
               .andExpect(jsonPath("$.director").value("Test director"))
               .andExpect(jsonPath("$.canComment").value(false));
    }

    @Test
    public void shouldReturnBadRequestWhenIdNotExist() throws Exception {
        Exception expectedException = new IllegalArgumentException("No movie with that id exists!");
        given(service.getMovieDetails(null, MOVIE_ID)).willThrow(expectedException);

        mockMvc.perform(get("/movie/" + MOVIE_ID))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(expectedException.getMessage()));
    }

    @Test
    @WithMockUser(username = UNIQUE_USERNAME)
    public void shouldReturnUpdatedQuizWhenIdExist() throws Exception {
        movie.setTitle("Updated title");
        given(service.updateMovie(anyString(), any(Movie.class))).willReturn(movie);

        mockMvc.perform(put("/movie/" + MOVIE_ID)
            .content(readFile("requests/movie-update.json"))
            .contentType(APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(MOVIE_ID))
               .andExpect(jsonPath("$.title").value(movie.getTitle()));
    }

    @Test
    @WithMockUser(username = UNIQUE_USERNAME)
    public void shouldReturnSuccessDeleteMessageWhenIdExist() throws Exception {
        given(service.deleteMovie(anyString())).willReturn("Movie successfully deleted");

        mockMvc.perform(delete("/movie/" + MOVIE_ID))
               .andExpect(status().isOk())
               .andExpect(content().string("Movie successfully deleted"));
    }
}