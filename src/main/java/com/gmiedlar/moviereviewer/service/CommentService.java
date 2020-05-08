package com.gmiedlar.moviereviewer.service;

import com.gmiedlar.moviereviewer.domain.Comment;

public interface CommentService {
    Comment addComment(String username, Comment comment);
    Comment getCommentById(String id);
    Comment updateComment(String id, Comment commentUpdate);
    String deleteComment(String id);
}
