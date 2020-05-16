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
public class CommentServiceAspect {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getName());

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.CommentServiceImpl.addComment(..)) && args(username, comment)")
    public Object invokeAddComment(ProceedingJoinPoint joinPoint, String username, Comment comment) throws Throwable {
        LOGGER.info("Trying to add new comment [rating={}, text={}] by user [username={}]",
            comment.getRating(), comment.getText(), username);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully added new comment.");
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while adding new comment: " + e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.CommentServiceImpl.getCommentById(..)) && args(id)")
    public Object invokeGetComment(ProceedingJoinPoint joinPoint, String id) throws Throwable {
        LOGGER.info("Trying to get comment [id={}]", id);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully get comment [id={}].", id);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while getting a comment: " + e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.CommentServiceImpl.updateComment(..)) && args(id, commentUpdate)")
    public Object invokeUpdateComment(ProceedingJoinPoint joinPoint, String id, Comment commentUpdate) throws Throwable {
        LOGGER.info("Trying to update comment [id={}] with values [rating={}, text={}]",
            id, commentUpdate.getRating(), commentUpdate.getText());
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully updated comment [id={}].", id);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while updating a comment: " + e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.CommentServiceImpl.deleteComment(..)) && args(id)")
    public Object invokeDeleteComment(ProceedingJoinPoint joinPoint, String id) throws Throwable {
        LOGGER.info("Trying to delete comment [id={}]", id);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully deleted comment [id={}].", id);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while deleting a comment: " + e.getMessage());
            throw e;
        }
    }
}
