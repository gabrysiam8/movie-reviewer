package com.gmiedlar.moviereviewer.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gmiedlar.moviereviewer.domain.Comment;
import com.gmiedlar.moviereviewer.domain.Movie;
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
import static com.gmiedlar.moviereviewer.common.TestData.MOVIE_ID;
import static com.gmiedlar.moviereviewer.common.TestData.UNIQUE_USERNAME;
import static com.gmiedlar.moviereviewer.common.TestData.USER_ID;
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
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService service;

    @MockBean
    private MovieService movieService;

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
    public void shouldReturnAllMovieComments() throws Exception {
        given(service.getMovieComments(anyString())).willReturn(List.of(COMMENT));

        mockMvc.perform(get("/review/" + MOVIE_ID))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(1))
               .andExpect(jsonPath("$[0].id").value(COMMENT_ID))
               .andExpect(jsonPath("$[0].rating").value(5))
               .andExpect(jsonPath("$[0].text").value("Awesome"))
               .andExpect(jsonPath("$[0].authorId").value(USER_ID));
    }

    @Test
    @WithMockUser(username = UNIQUE_USERNAME)
    public void shouldReturnMovieWithNewCommentWhenSuccessfullyAdded() throws Exception {
        String newCommentID = "newCommentId-1234";
        movie.getCommentIds().add(newCommentID);
        given(service.addMovieComment(anyString(), anyString(), any(Comment.class))).willReturn(movie);

        mockMvc.perform(post("/review/" + MOVIE_ID)
            .content("{\"rating\": 5,\"text\": \"Awesome\"}")
            .contentType(APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(MOVIE_ID))
               .andExpect(jsonPath("$.commentIds.length()").value(2))
               .andExpect(jsonPath("$.commentIds[0]").value(COMMENT_ID))
               .andExpect(jsonPath("$.commentIds[1]").value(newCommentID));
    }

    @Test
    @WithMockUser(username = UNIQUE_USERNAME)
    public void shouldReturnBadRequestWhenInvalidCommentContent() throws Exception {
        mockMvc.perform(post("/review/" + MOVIE_ID)
            .content("{}")
            .contentType(APPLICATION_JSON))
               .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = UNIQUE_USERNAME)
    public void shouldReturnUpdatedCommentWhenIdExist() throws Exception {
        movie.getCommentIds().add(COMMENT_ID);
        Comment commentUpdate = Comment.builder()
                                       .id(COMMENT_ID)
                                       .rating(5)
                                       .text("Updated comment")
                                       .authorId(USER_ID)
                                       .build();
        given(service.updateMovieComment(anyString(), anyString(), any(Comment.class))).willReturn(commentUpdate);

        mockMvc.perform(put("/review/" + MOVIE_ID + "/comment/" + COMMENT_ID)
            .content("{\"rating\": 5,\"text\": \"Updated comment\"}")
            .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(COMMENT_ID))
                .andExpect(jsonPath("$.text").value("Updated comment"))
                .andExpect(jsonPath("$.authorId").value(USER_ID));
    }

    @Test
    @WithMockUser(username = UNIQUE_USERNAME)
    public void shouldReturnSuccessDeleteMessageWhenCommentSuccessfullyDeleted() throws Exception {
        movie.getCommentIds().remove(COMMENT_ID);
        given(service.deleteMovieComment(MOVIE_ID, COMMENT_ID)).willReturn(movie);

        mockMvc.perform(delete("/review/" + MOVIE_ID + "/comment/" + COMMENT_ID))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(MOVIE_ID))
               .andExpect(jsonPath("$.commentIds.length()").value(0));
    }

    @Test
    @WithMockUser(username = UNIQUE_USERNAME)
    public void shouldReturnBadRequestWhenMovieNotFound() throws Exception {
        Exception expectedException = new IllegalArgumentException("No movie with that id exists!");
        given(service.deleteMovieComment(MOVIE_ID, COMMENT_ID)).willThrow(expectedException);

        mockMvc.perform(delete("/review/" + MOVIE_ID + "/comment/" + COMMENT_ID))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(expectedException.getMessage()));
    }

    @Test
    @WithMockUser(username = UNIQUE_USERNAME)
    public void shouldReturnBadRequestWhenCommentNotFound() throws Exception {
        Exception expectedException = new IllegalArgumentException("No comment with that id exists!");
        given(service.deleteMovieComment(MOVIE_ID, COMMENT_ID)).willThrow(expectedException);

        mockMvc.perform(delete("/review/" + MOVIE_ID + "/comment/" + COMMENT_ID))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(expectedException.getMessage()));
    }
}