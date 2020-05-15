package com.gmiedlar.moviereviewer.repository;

import java.util.List;

import com.gmiedlar.moviereviewer.domain.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieRepository extends MongoRepository<Movie, String> {
    List<Movie> findByUserId(String userId);
}
