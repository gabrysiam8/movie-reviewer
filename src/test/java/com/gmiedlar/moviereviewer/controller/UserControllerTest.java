package com.gmiedlar.moviereviewer.controller;

import com.gmiedlar.moviereviewer.dto.PasswordDto;
import com.gmiedlar.moviereviewer.dto.UserDto;
import com.gmiedlar.moviereviewer.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static com.gmiedlar.moviereviewer.common.TestData.UNIQUE_USERNAME;
import static com.gmiedlar.moviereviewer.common.TestData.USER_ID;
import static com.gmiedlar.moviereviewer.common.TestUtils.readFile;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnCurrentUserInfoWhenUsernameExist() throws Exception {
        UserDto userDto = new UserDto("test@gmail.com", UNIQUE_USERNAME, 2);
        given(service.getUserInfo(UNIQUE_USERNAME)).willReturn(userDto);

        mockMvc.perform(get("/user/me"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.email").value("test@gmail.com"))
               .andExpect(jsonPath("$.username").value(UNIQUE_USERNAME))
               .andExpect(jsonPath("$.moviesAdded").value(2));
    }

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnSuccessMessageWhenPasswordUpdated() throws Exception {
        String oldPassword = "pass";
        String newPassword = "newPass";
        PasswordDto passwordDto = new PasswordDto(oldPassword, newPassword, newPassword);
        given(service.updateUserPassword(UNIQUE_USERNAME, passwordDto)).willReturn("Password successfully changed");

        mockMvc.perform(put("/user/me/password")
            .content(readFile("requests/password.json"))
            .contentType(APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().string("Password successfully changed"));
    }

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnBadRequestWhenInvalidPassword() throws Exception {
        String oldPassword = "invalidPass";
        String newPassword = "newPass";
        PasswordDto passwordDto = new PasswordDto(oldPassword, newPassword, newPassword);
        Exception expectedException = new IllegalArgumentException("Wrong password!");
        given(service.updateUserPassword(UNIQUE_USERNAME, passwordDto)).willThrow(expectedException);

        mockMvc.perform(put("/user/me/password")
            .content(readFile("requests/password-invalid.json"))
            .contentType(APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(expectedException.getMessage()));
    }

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnSuccessMessageWhenPasswordReset() throws Exception {
        String newPassword = "newPass";
        PasswordDto passwordDto = new PasswordDto(null, newPassword, newPassword);
        given(service.resetUserPassword(USER_ID, passwordDto)).willReturn("Password successfully changed");

        mockMvc.perform(put("/user/"+USER_ID+"/password")
            .content("{\"newPassword\": \"newPass\",\"passwordConfirmation\": \"newPass\"}")
            .contentType(APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(content().string("Password successfully changed"));
    }

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnBadRequestWhenUserNotExist() throws Exception {
        Exception expectedException = new IllegalArgumentException("User with that id not exists!");
        String newPassword = "newPass";
        PasswordDto passwordDto = new PasswordDto(null, newPassword, newPassword);
        given(service.resetUserPassword("1234", passwordDto)).willThrow(expectedException);

        mockMvc.perform(put("/user/1234/password")
            .content("{\"newPassword\": \"newPass\",\"passwordConfirmation\": \"newPass\"}")
            .contentType(APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(expectedException.getMessage()));
    }

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnSuccessDeleteMessageWhenUsernameExist() throws Exception {
        given(service.deleteUser(UNIQUE_USERNAME)).willReturn("Account successfully deleted");

        mockMvc.perform(delete("/user/me"))
               .andExpect(status().isOk())
               .andExpect(content().string("Account successfully deleted"));
    }
}