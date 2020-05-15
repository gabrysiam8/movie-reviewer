package com.gmiedlar.moviereviewer.service;

import com.gmiedlar.moviereviewer.domain.Comment;
import com.gmiedlar.moviereviewer.domain.CustomUser;
import com.gmiedlar.moviereviewer.repository.CommentRepository;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;

    private final UserFinderService userFinderService;

    public CommentServiceImpl(CommentRepository repository, UserFinderService userFinderService) {
        this.repository = repository;
        this.userFinderService = userFinderService;
    }

    @Override
    public Comment addComment(String username, Comment comment) {
        CustomUser currentUser = userFinderService.findUserByUsername(username);
        comment.setAuthorId(currentUser.getId());
        return repository.save(comment);
    }

    @Override
    public Comment getCommentById(String id) {
        return repository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No comment with that id exists!"));
    }

    @Override
    public Comment updateComment(String id, Comment commentUpdate) {
        Comment comment = getCommentById(id);

        commentUpdate.setId(id);
        commentUpdate.setAuthorId(comment.getAuthorId());
        if(commentUpdate.equals(comment))
            return comment;

        return repository.save(commentUpdate);
    }

    @Override
    public String deleteComment(String id) {
        if(!repository.existsById(id))
            throw new IllegalArgumentException("No comment with that id exists!");
        repository.deleteById(id);
        return "Comment successfully deleted";
    }
}
