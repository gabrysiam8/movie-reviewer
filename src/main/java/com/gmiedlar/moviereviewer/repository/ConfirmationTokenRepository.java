package com.gmiedlar.moviereviewer.repository;

import com.gmiedlar.moviereviewer.domain.ConfirmationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfirmationTokenRepository extends MongoRepository<ConfirmationToken, String> {
    ConfirmationToken findByToken(String token);
}
