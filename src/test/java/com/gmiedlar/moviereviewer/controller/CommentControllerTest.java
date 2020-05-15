package com.gmiedlar.moviereviewer.controller;

import com.gmiedlar.moviereviewer.service.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.gmiedlar.moviereviewer.common.TestData.COMMENT;
import static com.gmiedlar.moviereviewer.common.TestData.COMMENT_ID;
import static com.gmiedlar.moviereviewer.common.TestData.USER_ID;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService service;

    @Test
    public void shouldReturnCommentWhenIdExist() throws Exception {
        given(service.getCommentById(anyString())).willReturn(COMMENT);

        mockMvc.perform(get("/comment/" + COMMENT_ID))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(COMMENT_ID))
               .andExpect(jsonPath("$.rating").value(5))
               .andExpect(jsonPath("$.text").value("Awesome"))
               .andExpect(jsonPath("$.authorId").value(USER_ID));
    }

    @Test
    public void shouldReturnBadRequestWhenIdNotExist() throws Exception {
        Exception expectedException = new IllegalArgumentException("No comment with that id exists!");
        given(service.getCommentById(anyString())).willThrow(expectedException);

        mockMvc.perform(get("/comment/invalidId-1234"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(expectedException.getMessage()));
    }
}