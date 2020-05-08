package com.gmiedlar.moviereviewer.service;

import java.util.Calendar;

import com.gmiedlar.moviereviewer.domain.ConfirmationToken;
import com.gmiedlar.moviereviewer.repository.ConfirmationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.gmiedlar.moviereviewer.common.TestData.CONFIRMATION_TOKEN;
import static com.gmiedlar.moviereviewer.common.TestData.DISABLED_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceImplTest {

    @Mock
    private ConfirmationTokenRepository tokenRepository;

    private ConfirmationTokenServiceImpl tokenService;

    private ConfirmationToken confirmationToken;

    @BeforeEach
    public void setUp() {
        tokenService = new ConfirmationTokenServiceImpl(tokenRepository);

        confirmationToken = new ConfirmationToken(CONFIRMATION_TOKEN, DISABLED_USER);
    }

    @Test
    public void shouldCreateToken() {
        //given
        given(tokenRepository.save(any(ConfirmationToken.class))).willReturn(confirmationToken);

        //when
        ConfirmationToken result = tokenService.createToken(DISABLED_USER);

        //then
        verify(tokenRepository, times(1)).save(any(ConfirmationToken.class));
        assertNotNull(result);
        assertEquals(CONFIRMATION_TOKEN, result.getToken());
    }

    @Test
    public void shouldReturnToken() {
        //given
        given(tokenRepository.findByToken(CONFIRMATION_TOKEN)).willReturn(confirmationToken);

        //when
        ConfirmationToken result = tokenService.getConfirmationToken(CONFIRMATION_TOKEN);

        //then
        verify(tokenRepository, times(1)).findByToken(anyString());
        assertNotNull(result);
        assertEquals(CONFIRMATION_TOKEN, result.getToken());
    }

    @Test
    public void shouldConfirmToken() {
        //given
        given(tokenRepository.findByToken(CONFIRMATION_TOKEN)).willReturn(confirmationToken);

        //when
        ConfirmationToken result = tokenService.confirmToken(CONFIRMATION_TOKEN);

        //then
        verify(tokenRepository, times(1)).findByToken(anyString());
        assertNotNull(result);
        assertEquals(CONFIRMATION_TOKEN, result.getToken());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenTokenNotExist() {
        //given
        given(tokenRepository.findByToken(CONFIRMATION_TOKEN)).willReturn(null);

        Throwable exception = assertThrows(
            IllegalArgumentException.class,
            //when
            () -> tokenService.confirmToken(CONFIRMATION_TOKEN)
        );

        //then
        verify(tokenRepository, times(1)).findByToken(anyString());
        assertEquals("Invalid token.", exception.getMessage());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenTokenExpired() {
        //given
        ConfirmationToken confirmationToken = new ConfirmationToken(CONFIRMATION_TOKEN, DISABLED_USER);
        confirmationToken.setExpirationDate(Calendar.getInstance().getTime());
        given(tokenRepository.findByToken(CONFIRMATION_TOKEN)).willReturn(confirmationToken);

        Throwable exception = assertThrows(
            IllegalArgumentException.class,
            //when
            () -> tokenService.confirmToken(CONFIRMATION_TOKEN)
        );
        //then
        verify(tokenRepository, times(1)).findByToken(anyString());
        assertEquals("Token have expired.", exception.getMessage());
    }
}