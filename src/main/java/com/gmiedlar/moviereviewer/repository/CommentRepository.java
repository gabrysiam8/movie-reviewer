package com.gmiedlar.moviereviewer.repository;

import com.gmiedlar.moviereviewer.domain.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<Comment, String> {

}
