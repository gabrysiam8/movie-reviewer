package com.gmiedlar.moviereviewer.aspect;

import com.gmiedlar.moviereviewer.domain.Comment;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ReviewServiceAspect {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getName());

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.MovieServiceImpl.getMovieComments(..)) && args(movieId)")
    public Object invokeGetAllMovieComment(ProceedingJoinPoint joinPoint, String movieId) throws Throwable {
        LOGGER.info("Trying to get all movie [id={}] comments...", movieId);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully retrieved all movie [id={}] comments.", movieId);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while getting all movie comments: " + e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.MovieServiceImpl.addMovieComment(..)) && args(username, id, comment)")
    public Object invokeAddMovieComment(ProceedingJoinPoint joinPoint, String username, String id, Comment comment) throws Throwable {
        LOGGER.info("Trying to add new comment [rating={}, text={}] to movie [id={}] by user [username={}].",
            comment.getRating(), comment.getText(), id, username);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully added new comment to movie [id={}].", id);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while adding new comment to movie: " + e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.MovieServiceImpl.updateMovieComment(..)) && args(id, commentId, commentUpdate)")
    public Object invokeUpdateMovieComment(ProceedingJoinPoint joinPoint, String id, String commentId, Comment commentUpdate) throws Throwable {
        LOGGER.info("Trying to update comment [id={}] to movie [id={}] with values [rating={}, text={}].",
            commentId, id, commentUpdate.getRating(), commentUpdate.getText());
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully updated comment [id={}] to movie [id={}].", commentId, id);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while updating comment to movie: " + e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.MovieServiceImpl.deleteMovieComment()) && args(movieId, commentId)")
    public Object invokeDeleteMovieComment(ProceedingJoinPoint joinPoint, String movieId, String commentId) throws Throwable {
        LOGGER.info("Trying to delete comment [id={}] to movie [id={}].", commentId, movieId);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully deleted comment [id={}] to movie [id={}].", commentId, movieId);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while deleting movie comment: " + e.getMessage());
            throw e;
        }
    }
}
