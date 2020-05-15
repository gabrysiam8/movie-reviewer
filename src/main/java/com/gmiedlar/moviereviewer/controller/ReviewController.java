package com.gmiedlar.moviereviewer.controller;

import java.util.List;
import javax.validation.Valid;

import com.gmiedlar.moviereviewer.domain.Comment;
import com.gmiedlar.moviereviewer.domain.Movie;
import com.gmiedlar.moviereviewer.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review/{movieId}")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getMovieReviews(@PathVariable(value="movieId") String id) {
        List<Comment> comments =  service.getMovieComments(id);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addMovieReview(@AuthenticationPrincipal UserDetails userDetails, @PathVariable(value="movieId") String id, @Valid @RequestBody Comment comment) {
        Movie newMovie = service.addMovieComment(userDetails.getUsername(), id, comment);
        return new ResponseEntity<>(newMovie, HttpStatus.OK);
    }

    @PutMapping("/comment/{commentId}")
    public ResponseEntity<?> updateMovieReview(@PathVariable(value="movieId") String movieId, @PathVariable(value="commentId") String commentId, @Valid @RequestBody Comment comment) {
        Comment updatedComment = service.updateMovieComment(movieId, commentId, comment);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> deleteMovieReview(@PathVariable(value="movieId") String movieId, @PathVariable(value="commentId") String commentId) {
        try {
            Movie updatedMovie = service.deleteMovieComment(movieId, commentId);
            return new ResponseEntity<>(updatedMovie, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
