package com.gmiedlar.moviereviewer.controller;

import javax.validation.Valid;

import com.gmiedlar.moviereviewer.domain.Movie;
import com.gmiedlar.moviereviewer.service.MovieService;
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
@RequestMapping("/movie")
public class MovieController {

    private final MovieService service;

    public MovieController(MovieService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> addMovie(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody Movie movie) {
        Movie newMovie =  service.addMovie(userDetails.getUsername(), movie);
        return new ResponseEntity<>(newMovie, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllMovies() {
        return new ResponseEntity<>(service.getAllMovies(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllUserMovies(@AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(service.getAllUserMovies(userDetails.getUsername()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable(value="id") String id) {
        try {
            return new ResponseEntity<>(service.getMovieById(id), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable(value="id") String id, @Valid @RequestBody Movie movie) {
        try {
            return new ResponseEntity<>(service.updateMovie(id, movie), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable(value="id") String id) {
        try {
            String msg = service.deleteMovie(id);
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
