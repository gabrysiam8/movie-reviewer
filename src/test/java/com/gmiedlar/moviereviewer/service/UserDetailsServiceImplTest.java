package com.gmiedlar.moviereviewer.service;

import java.util.Optional;

import com.gmiedlar.moviereviewer.domain.CustomUser;
import com.gmiedlar.moviereviewer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.gmiedlar.moviereviewer.common.TestData.ENABLED_USER;
import static com.gmiedlar.moviereviewer.common.TestData.UNIQUE_USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    public void setUp() {
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    public void shouldLoadUser() {
        //given
        given(userRepository.findByEmail(UNIQUE_USERNAME)).willReturn(Optional.empty());
        given(userRepository.findByUsername(UNIQUE_USERNAME)).willReturn(Optional.of(ENABLED_USER));

        //when
        UserDetails result = userDetailsService.loadUserByUsername(UNIQUE_USERNAME);

        //then
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).findByUsername(anyString());
        assertNotNull(result);
        assertEquals(UNIQUE_USERNAME, result.getUsername());
    }

    @Test
    public void shouldThrowUsernameNotFoundExceptionWhenUserNotExist() {
        //given
        given(userRepository.findByEmail(UNIQUE_USERNAME)).willReturn(Optional.empty());
        given(userRepository.findByUsername(UNIQUE_USERNAME)).willReturn(Optional.empty());

        //when
        Throwable exception = assertThrows(
            UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername(UNIQUE_USERNAME)
        );

        //then
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).findByUsername(anyString());
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void shouldFindUserByUsername() {
        //given
        given(userRepository.findByUsername(UNIQUE_USERNAME)).willReturn(Optional.of(ENABLED_USER));

        //when
        CustomUser result = userDetailsService.findUserByUsername(UNIQUE_USERNAME);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        assertNotNull(result);
        assertEquals(UNIQUE_USERNAME, result.getUsername());
    }

    @Test
    public void shouldThrowUsernameNotFoundExceptionWhenUsernameNotExist() {
        //given
        given(userRepository.findByUsername(UNIQUE_USERNAME)).willReturn(Optional.empty());

        //when
        Throwable exception = assertThrows(
            UsernameNotFoundException.class,
            () -> userDetailsService.findUserByUsername(UNIQUE_USERNAME)
        );

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        assertEquals("No user with that username exists!", exception.getMessage());
    }
}