package com.gmiedlar.moviereviewer.controller;

import javax.validation.Valid;

import com.gmiedlar.moviereviewer.domain.Comment;
import com.gmiedlar.moviereviewer.service.CommentService;
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
@RequestMapping("/comment")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> addComment(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody Comment comment) {
        Comment newComment =  service.addComment(userDetails.getUsername(), comment);
        return new ResponseEntity<>(newComment, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable(value="id") String id) {
        try {
            return new ResponseEntity<>(service.getCommentById(id), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable(value="id") String id, @Valid @RequestBody Comment comment) {
        try {
            return new ResponseEntity<>(service.updateComment(id, comment), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable(value="id") String id) {
        try {
            String msg = service.deleteComment(id);
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
