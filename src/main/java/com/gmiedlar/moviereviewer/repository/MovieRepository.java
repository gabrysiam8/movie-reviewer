package com.gmiedlar.moviereviewer.repository;

import com.gmiedlar.moviereviewer.domain.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieRepository extends MongoRepository<Movie, String> {

}
