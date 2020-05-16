package com.gmiedlar.moviereviewer.aspect;

import com.gmiedlar.moviereviewer.dto.UserLoginDto;
import com.gmiedlar.moviereviewer.dto.UserRegisterDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthServiceAspect {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getName());

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.AuthServiceImpl.registerUser(..)) && args(user)")
    public Object invokeRegister(ProceedingJoinPoint joinPoint, UserRegisterDto user) throws Throwable {
        LOGGER.info("Trying to register new user [username={}, email={}]", user.getUsername(), user.getEmail());
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully registered new user [username={}].", user.getUsername());
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while registering new user: "+ e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.AuthServiceImpl.loginUser(..)) && args(user)")
    public Object invokeLogin(ProceedingJoinPoint joinPoint, UserLoginDto user) throws Throwable {
        LOGGER.info("Trying to log in user [username={}]", user.getUsername());
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully logged in user [username={}]", user.getUsername());
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while log in user: "+ e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.AuthServiceImpl.confirmUserAccount(..)) && args(token)")
    public Object invokeConfirmUserAccount(ProceedingJoinPoint joinPoint, String token) throws Throwable {
        LOGGER.info("Trying to confirm user account with token [token={}]", token);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully confirmed user account.");
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while confirming user account: "+ e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.AuthServiceImpl.sendResetPasswordEmail(..)) && args(email)")
    public Object invokeSendResetPasswordEmail(ProceedingJoinPoint joinPoint, String email) throws Throwable {
        LOGGER.info("Trying to send reset password email to user [email={}]", email);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully send reset password email to user [email={}].", email);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while sending reset password email to user: "+ e.getMessage());
            throw e;
        }
    }
}
