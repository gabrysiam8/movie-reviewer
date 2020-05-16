package com.gmiedlar.moviereviewer.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserServiceAspect {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getName());

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.UserServiceImpl.getUserByUsername(..)) && args(username)")
    public Object invokeGetUserByUsername(ProceedingJoinPoint joinPoint, String username) throws Throwable {
        LOGGER.info("Trying to get user [username={}]", username);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully retrieved a user [username={}].", username);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while retrieving a user: "+ e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.UserServiceImpl.getUserById(..)) && args(id)")
    public Object invokeGetUserById(ProceedingJoinPoint joinPoint, String id) throws Throwable {
        LOGGER.info("Trying to get user [id={}]", id);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully retrieved a user [id={}].", id);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while retrieving a user: "+ e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.UserServiceImpl.updateUserPassword(..)) && args(username,*)")
    public Object invokeUpdateUserPassword(ProceedingJoinPoint joinPoint, String username) throws Throwable {
        LOGGER.info("Trying to update user [username={}] password", username);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully updated user [username={}] password.", username);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while updating user password: "+ e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.UserServiceImpl.resetUserPassword(..)) && args(id,*)")
    public Object invokeResetUserPassword(ProceedingJoinPoint joinPoint, String id) throws Throwable {
        LOGGER.info("Trying to reset user [id={}] password", id);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully reset user [id={}] password.", id);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while resetting user password: "+ e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.UserServiceImpl.deleteUser(..)) && args(username)")
    public Object invokeDeleteUser(ProceedingJoinPoint joinPoint, String username) throws Throwable {
        LOGGER.info("Trying to delete user [username={}]", username);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully deleted user [username={}].", username);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while deleting user: "+ e.getMessage());
            throw e;
        }
    }
}
