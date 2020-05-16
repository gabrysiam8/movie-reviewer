package com.gmiedlar.moviereviewer.aspect;

import com.gmiedlar.moviereviewer.domain.Movie;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MovieServiceAspect {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getName());

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.MovieServiceImpl.addMovie(..)) && args(username, movie)")
    public Object invokeAddMovie(ProceedingJoinPoint joinPoint, String username, Movie movie) throws Throwable {
        LOGGER.info("Trying to add new movie [title={}, genre={}, year={}, director={}] by user [username={}]",
            movie.getTitle(), movie.getGenre(), movie.getYear(), movie.getDirector(), username);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully added new movie [title={}] by user [username={}].", movie.getTitle(), username);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while adding new movie: " + e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.MovieServiceImpl.getAllMovies(..))")
    public Object invokeGetAllMovies(ProceedingJoinPoint joinPoint) throws Throwable {
        LOGGER.info("Trying to get all movies...");
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully retrieved all movies.");
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while getting all movies: " + e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.MovieServiceImpl.getAllUserMovies(..)) && args(username)")
    public Object invokeGetAllUserMovies(ProceedingJoinPoint joinPoint, String username) throws Throwable {
        LOGGER.info("Trying to get all user [username={}] movies.", username);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully retrieved all user [username={}] movies.", username);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while getting all user movies: " + e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.MovieServiceImpl.updateMovie(..)) && args(id, movieUpdate)")
    public Object invokeUpdateMovie(ProceedingJoinPoint joinPoint, String id, Movie movieUpdate) throws Throwable {
        LOGGER.info("Trying to update movie [id={}] with values [title={}, genre={}, year={}, director={}]",
            id, movieUpdate.getTitle(), movieUpdate.getGenre(), movieUpdate.getYear(), movieUpdate.getDirector());
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully updated movie [id={}].", id);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while updating a movie: " + e.getMessage());
            throw e;
        }
    }

    @Around(value = "execution(* com.gmiedlar.moviereviewer.service.MovieServiceImpl.deleteMovie(..)) && args(id)")
    public Object invokeDeleteMovie(ProceedingJoinPoint joinPoint, String id) throws Throwable {
        LOGGER.info("Trying to delete movie [id={}]", id);
        try {
            Object result = joinPoint.proceed();
            LOGGER.info("Successfully deleted movie [id={}].", id);
            return result;
        } catch(Throwable e) {
            LOGGER.warn("Exception thrown while deleting a movie: " + e.getMessage());
            throw e;
        }
    }
}
