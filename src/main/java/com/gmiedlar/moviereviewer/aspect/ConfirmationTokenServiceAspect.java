package com.gmiedlar.moviereviewer.aspect;

import com.gmiedlar.moviereviewer.domain.CustomUser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ConfirmationTokenServiceAspect {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getName());

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.ConfirmationTokenServiceImpl.createToken(..)) && args(user)")
    public Object invokeCreateToken(ProceedingJoinPoint joinPoint, CustomUser user) throws Throwable {
        LOGGER.info("Trying to create confirmation token for user [username={}, email={}]",
            user.getUsername(), user.getEmail());
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully created new token for user [username={}].", user.getUsername());
            return result;
        } catch (Throwable e) {
            LOGGER.warn("Exception thrown while creating new token: " + e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.ConfirmationTokenServiceImpl.getConfirmationToken(..)) && args(token)")
    public Object invokeGetConfirmationToken(ProceedingJoinPoint joinPoint, String token) throws Throwable {
        LOGGER.info("Trying to get token [token={}]", token);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully retrieved token [token={}].", token);
            return result;
        } catch (Throwable e) {
            LOGGER.warn("Exception thrown while getting a token: " + e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.ConfirmationTokenServiceImpl.confirmToken(..)) && args(token)")
    public Object invokeConfirmToken(ProceedingJoinPoint joinPoint, String token) throws Throwable {
        LOGGER.info("Trying to confirm token [token={}]", token);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully confirmed token [token={}].", token);
            return result;
        } catch (Throwable e) {
            LOGGER.warn("Exception thrown while confirming a token: " + e.getMessage());
            throw e;
        }
    }
}
