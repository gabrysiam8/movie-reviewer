package com.gmiedlar.moviereviewer.service;

import java.util.Map;
import javax.mail.MessagingException;

import com.gmiedlar.moviereviewer.domain.CustomUser;
import com.gmiedlar.moviereviewer.dto.UserLoginDto;
import com.gmiedlar.moviereviewer.dto.UserRegisterDto;

public interface AuthService {

    CustomUser registerUser(UserRegisterDto user) throws MessagingException;
    Map<String,String> loginUser(UserLoginDto userDto);
    String confirmUserAccount(String token);
    String sendResetPasswordEmail(String email) throws MessagingException;
}
