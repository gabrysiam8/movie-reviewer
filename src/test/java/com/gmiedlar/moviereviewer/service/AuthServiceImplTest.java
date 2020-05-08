package com.gmiedlar.moviereviewer.service;

import java.util.Map;
import java.util.Optional;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import com.gmiedlar.moviereviewer.config.JwtTokenProvider;
import com.gmiedlar.moviereviewer.domain.ConfirmationToken;
import com.gmiedlar.moviereviewer.domain.CustomUser;
import com.gmiedlar.moviereviewer.dto.EmailDto;
import com.gmiedlar.moviereviewer.dto.UserLoginDto;
import com.gmiedlar.moviereviewer.dto.UserRegisterDto;
import com.gmiedlar.moviereviewer.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.gmiedlar.moviereviewer.common.TestData.CONFIRMATION_TOKEN;
import static com.gmiedlar.moviereviewer.common.TestData.DISABLED_USER;
import static com.gmiedlar.moviereviewer.common.TestData.ENABLED_USER;
import static com.gmiedlar.moviereviewer.common.TestData.UNIQUE_EMAIL;
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
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private ConfirmationTokenService confirmationTokenService;

    @Mock
    private EmailSenderService emailSenderService;

    private AuthServiceImpl authServiceImpl;

    private UserRegisterDto userRegisterDto;

    private UserLoginDto userLoginDto;

    @BeforeEach
    public void setUp() {
        authServiceImpl = new AuthServiceImpl(userRepository, authenticationManager, passwordEncoder, tokenProvider, confirmationTokenService, emailSenderService);

        userRegisterDto = UserRegisterDto.builder()
                                         .email(UNIQUE_EMAIL)
                                         .username(UNIQUE_USERNAME)
                                         .password("pass")
                                         .passwordConfirmation("pass")
                                         .build();

        userLoginDto = UserLoginDto.builder()
                                   .username(UNIQUE_USERNAME)
                                   .password("pass")
                                   .build();
    }

    @Test
    public void shouldRegisterUser() throws MessagingException {
        //given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.save(any(CustomUser.class))).willReturn(DISABLED_USER);
        given(confirmationTokenService.createToken(any(CustomUser.class))).willReturn(new ConfirmationToken("token", DISABLED_USER));
        given(emailSenderService.createMimeMessage(any(EmailDto.class))).willReturn(new MimeMessage((Session) null));

        //when
        CustomUser result = authServiceImpl.registerUser(userRegisterDto);

        //then
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).existsByUsername(anyString());
        verify(userRepository, times(1)).save(any(CustomUser.class));
        verify(confirmationTokenService, times(1)).createToken(any(CustomUser.class));
        verify(emailSenderService, times(1)).createMimeMessage(any(EmailDto.class));
        verify(emailSenderService, times(1)).sendEmail(any(MimeMessage.class));
        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals(UNIQUE_USERNAME, result.getUsername());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenEmailAlreadyTaken() throws MessagingException {
        //given
        given(userRepository.existsByEmail(anyString())).willReturn(true);

        Throwable exception = assertThrows(
            IllegalArgumentException.class,
            //when
            () -> authServiceImpl.registerUser(userRegisterDto)
        );

        //then
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(CustomUser.class));
        verify(confirmationTokenService, never()).createToken(any(CustomUser.class));
        verify(emailSenderService, never()).createMimeMessage(any(EmailDto.class));
        verify(emailSenderService, never()).sendEmail(any(MimeMessage.class));
        assertEquals("User with this email already exists!", exception.getMessage());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenUsernameAlreadyTaken() throws MessagingException {
        //given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.existsByUsername(anyString())).willReturn(true);

        Throwable exception = assertThrows(
            IllegalArgumentException.class,
            //when
            () -> authServiceImpl.registerUser(userRegisterDto)
        );

        //then
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).existsByUsername(anyString());
        verify(userRepository, never()).save(any(CustomUser.class));
        verify(confirmationTokenService, never()).createToken(any(CustomUser.class));
        verify(emailSenderService, never()).createMimeMessage(any(EmailDto.class));
        verify(emailSenderService, never()).sendEmail(any(MimeMessage.class));
        assertEquals("User with this username already exists!", exception.getMessage());
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenTokenServiceReturnNull() throws MessagingException {
        //given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.save(any(CustomUser.class))).willReturn(DISABLED_USER);
        given(confirmationTokenService.createToken(any(CustomUser.class))).willReturn(null);

        assertThrows(
            NullPointerException.class,
            //when
            () -> authServiceImpl.registerUser(userRegisterDto)
        );

        //then
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).existsByUsername(anyString());
        verify(userRepository, times(1)).save(any(CustomUser.class));
        verify(confirmationTokenService, times(1)).createToken(any(CustomUser.class));
        verify(emailSenderService, never()).createMimeMessage(any(EmailDto.class));
        verify(emailSenderService, never()).sendEmail(any(MimeMessage.class));
    }

    @Test
    public void shouldThrowMessagingExceptionWhenEmailServiceThrowException() throws MessagingException {
        //given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.save(any(CustomUser.class))).willReturn(DISABLED_USER);
        given(confirmationTokenService.createToken(any(CustomUser.class))).willReturn(new ConfirmationToken("token", DISABLED_USER));
        given(emailSenderService.createMimeMessage(any(EmailDto.class))).willThrow(MessagingException.class);

        assertThrows(
            MessagingException.class,
            //when
            () -> authServiceImpl.registerUser(userRegisterDto)
        );

        //then
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).existsByUsername(anyString());
        verify(userRepository, times(1)).save(any(CustomUser.class));
        verify(confirmationTokenService, times(1)).createToken(any(CustomUser.class));
        verify(emailSenderService, times(1)).createMimeMessage(any(EmailDto.class));
        verify(emailSenderService, never()).sendEmail(any(MimeMessage.class));
        verify(userRepository, times(1)).delete(any(CustomUser.class));
    }

    @Test
    public void shouldLoginUser(){
        given(userRepository.findByUsername(UNIQUE_USERNAME)).willReturn(Optional.of(ENABLED_USER));
        given(tokenProvider.generateToken(ENABLED_USER)).willReturn(CONFIRMATION_TOKEN);

        //when
        Map<String,String> result = authServiceImpl.loginUser(userLoginDto);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(tokenProvider, times(1)).generateToken(any(CustomUser.class));
        assertNotNull(result);
        assertEquals(CONFIRMATION_TOKEN, result.get("token"));
    }

    @Test
    public void shouldThrowDisabledExceptionWhenUserDisabled(){
        //given
        given(userRepository.findByUsername(UNIQUE_USERNAME)).willReturn(Optional.of(DISABLED_USER));

        Throwable exception = assertThrows(
            DisabledException.class,
            //when
            () -> authServiceImpl.loginUser(userLoginDto)
        );

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(tokenProvider, never()).generateToken(any(CustomUser.class));
        assertEquals("User account is locked!", exception.getMessage());
    }

    @Test
    public void shouldConfirmUserAccount() {
        //given
        ConfirmationToken confirmationToken = new ConfirmationToken(CONFIRMATION_TOKEN, DISABLED_USER);

        given(confirmationTokenService.confirmToken(anyString())).willReturn(confirmationToken);

        //when
        String result = authServiceImpl.confirmUserAccount(CONFIRMATION_TOKEN);

        //then
        verify(confirmationTokenService, times(1)).confirmToken(anyString());
        assertNotNull(result);
        assertEquals(result, "Account successfully verified.");
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenTokenNotExist() {
        //given
        Exception expectedException = new IllegalArgumentException("Invalid token.");
        given(confirmationTokenService.confirmToken(anyString())).willThrow(expectedException);

        Throwable exception = assertThrows(
            IllegalArgumentException.class,
            //when
            () -> authServiceImpl.confirmUserAccount(CONFIRMATION_TOKEN)
        );

        //then
        verify(confirmationTokenService, times(1)).confirmToken(anyString());
        assertEquals("Invalid token.", exception.getMessage());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenTokenExpired() {
        //given
        Exception expectedException = new IllegalArgumentException("Token have expired.");
        given(confirmationTokenService.confirmToken(anyString())).willThrow(expectedException);

        Throwable exception = assertThrows(
            IllegalArgumentException.class,
            //when
            () -> authServiceImpl.confirmUserAccount(CONFIRMATION_TOKEN)
        );

        //then
        verify(confirmationTokenService, times(1)).confirmToken(anyString());
        assertEquals("Token have expired.", exception.getMessage());
    }

    @Test
    public void shouldSendResetPasswordEmail() throws MessagingException {
        //given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(ENABLED_USER));
        given(confirmationTokenService.createToken(any(CustomUser.class))).willReturn(new ConfirmationToken("token", ENABLED_USER));
        given(emailSenderService.createMimeMessage(any(EmailDto.class))).willReturn(new MimeMessage((Session) null));

        //when
        String result = authServiceImpl.sendResetPasswordEmail(UNIQUE_EMAIL);

        //then
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(confirmationTokenService, times(1)).createToken(any(CustomUser.class));
        verify(emailSenderService, times(1)).createMimeMessage(any(EmailDto.class));
        verify(emailSenderService, times(1)).sendEmail(any(MimeMessage.class));
        assertNotNull(result);
        assertEquals(result, "Email successfully send");
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenEmailNotExist() throws MessagingException {
        //given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        Throwable exception = assertThrows(
            IllegalArgumentException.class,
            //when
            () -> authServiceImpl.sendResetPasswordEmail("invalid@gmail.com")
        );

        //then
        verify(userRepository, times(1)).findByEmail(anyString());
        assertEquals("User with that email not exists!", exception.getMessage());
    }
}