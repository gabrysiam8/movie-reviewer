package com.gmiedlar.moviereviewer.controller;

import com.gmiedlar.moviereviewer.domain.ConfirmationToken;
import com.gmiedlar.moviereviewer.service.ConfirmationTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.gmiedlar.moviereviewer.common.TestData.CONFIRMATION_TOKEN;
import static com.gmiedlar.moviereviewer.common.TestData.DISABLED_USER;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ConfirmationTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfirmationTokenService service;

    @Test
    public void shouldReturnConfirmationTokenWhenSuccessfullyConfirmed() throws Exception {
        ConfirmationToken confirmationToken = new ConfirmationToken(CONFIRMATION_TOKEN, DISABLED_USER);
        given(service.confirmToken(anyString())).willReturn(confirmationToken);

        mockMvc.perform(get("/confirmation")
            .param("token",CONFIRMATION_TOKEN))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").value(CONFIRMATION_TOKEN))
               .andExpect(jsonPath("$.user.username").value(DISABLED_USER.getUsername()));
    }

    @Test
    public void shouldReturnBadRequestWhenInvalidToken() throws Exception {
        Exception expectedException = new IllegalArgumentException("Invalid token.");
        given(service.confirmToken(anyString())).willThrow(expectedException);

        mockMvc.perform(get("/confirmation")
            .param("token", "invalidToken"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(expectedException.getMessage()));
    }
}