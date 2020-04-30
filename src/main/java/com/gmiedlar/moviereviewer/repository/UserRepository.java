package com.gmiedlar.moviereviewer.repository;

import java.util.Optional;

import com.gmiedlar.moviereviewer.domain.CustomUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<CustomUser, String> {
    Optional<CustomUser> findByEmail(String email);
    Optional<CustomUser> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
