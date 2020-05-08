package com.gmiedlar.moviereviewer.service;

import java.util.Collections;
import java.util.Optional;

import com.gmiedlar.moviereviewer.domain.CustomUser;
import com.gmiedlar.moviereviewer.dto.PasswordDto;
import com.gmiedlar.moviereviewer.dto.UserDto;
import com.gmiedlar.moviereviewer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.gmiedlar.moviereviewer.common.TestData.ENABLED_USER;
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
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MovieService movieService;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, movieService);
    }

    @Test
    public void shouldReturnUserInfo() {
        //given
        given(userRepository.findByUsername(UNIQUE_USERNAME)).willReturn(Optional.of(ENABLED_USER));
        given(movieService.getAllMovies()).willReturn(Collections.emptyList());

        //when
        UserDto result = userService.getUserInfo(UNIQUE_USERNAME);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(movieService, times(1)).getAllMovies();
        assertNotNull(result);
        assertEquals(ENABLED_USER.getEmail(), result.getEmail());
        assertEquals(0, result.getMoviesAdded());
    }

    @Test
    public void shouldThrowUsernameNotFoundExceptionWhenUserNotExist() {
        //given
        given(userRepository.findByUsername(UNIQUE_USERNAME)).willReturn(Optional.empty());

        Throwable exception = assertThrows(
            UsernameNotFoundException.class,
            //when
            () -> userService.getUserInfo(UNIQUE_USERNAME)
        );

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(movieService, never()).getAllMovies();
        assertEquals("No user with that username exists!", exception.getMessage());
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenMovieServiceReturnNull() {
        //given
        given(userRepository.findByUsername(UNIQUE_USERNAME)).willReturn(Optional.of(ENABLED_USER));
        given(movieService.getAllMovies()).willReturn(null);

        Throwable exception = assertThrows(
            NullPointerException.class,
            //when
            () -> userService.getUserInfo(UNIQUE_USERNAME)
        );

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(movieService, times(1)).getAllMovies();
    }

    @Test
    public void shouldUpdateUserPassword() {
        //given
        String oldPassword = "pass";
        String newPassword = "newPass";
        PasswordDto passwordDto = new PasswordDto(oldPassword, newPassword, newPassword);
        given(userRepository.findByUsername(UNIQUE_USERNAME)).willReturn(Optional.of(ENABLED_USER));
        given(passwordEncoder.matches(passwordDto.getOldPassword(), ENABLED_USER.getPassword())).willReturn(true);

        //when
        String result = userService.updateUserPassword(UNIQUE_USERNAME, passwordDto);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(CustomUser.class));
        assertNotNull(result);
        assertEquals(result, "Password successfully changed");
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenInvalidOldPassword() {
        //given
        String oldPassword = "invalidPass";
        String newPassword = "newPass";
        PasswordDto passwordDto = new PasswordDto(oldPassword, newPassword, newPassword);
        given(userRepository.findByUsername(UNIQUE_USERNAME)).willReturn(Optional.of(ENABLED_USER));
        given(passwordEncoder.matches(passwordDto.getOldPassword(), ENABLED_USER.getPassword())).willReturn(false);


        Throwable exception = assertThrows(
            IllegalArgumentException.class,
            //when
            () -> userService.updateUserPassword(UNIQUE_USERNAME, passwordDto)
        );

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(CustomUser.class));
        assertEquals("Wrong password!", exception.getMessage());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenInvalidNewPasswordConfirmation() {
        //given
        String oldPassword = "pass";
        String newPassword = "newPass";
        PasswordDto passwordDto = new PasswordDto(oldPassword, newPassword, "invalidConfirmation");
        given(userRepository.findByUsername(UNIQUE_USERNAME)).willReturn(Optional.of(ENABLED_USER));
        given(passwordEncoder.matches(passwordDto.getOldPassword(), ENABLED_USER.getPassword())).willReturn(true);

        Throwable exception = assertThrows(
            IllegalArgumentException.class,
            //when
            () -> userService.updateUserPassword(UNIQUE_USERNAME, passwordDto)
        );

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(CustomUser.class));
        assertEquals("The Password confirmation must match New password!", exception.getMessage());
    }

    @Test
    public void shouldResetUserPassword() {
        //given
        String newPassword = "newPass";
        PasswordDto passwordDto = new PasswordDto(null, newPassword, newPassword);
        given(userRepository.findById(USER_ID)).willReturn(Optional.of(ENABLED_USER));

        //when
        String result = userService.resetUserPassword(USER_ID, passwordDto);

        //then
        verify(userRepository, times(1)).findById(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(CustomUser.class));
        assertNotNull(result);
        assertEquals(result, "Password successfully changed");
    }

    @Test
    public void shouldDeleteUser() {
        //given
        given(userRepository.findByUsername(UNIQUE_USERNAME)).willReturn(Optional.of(ENABLED_USER));

        //when
        String result = userService.deleteUser(UNIQUE_USERNAME);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(userRepository, times(1)).delete(any(CustomUser.class));
        assertNotNull(result);
        assertEquals(result, "Account successfully deleted");
    }
}