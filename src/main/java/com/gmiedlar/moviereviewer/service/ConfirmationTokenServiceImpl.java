package com.gmiedlar.moviereviewer.service;

import java.util.Calendar;
import java.util.UUID;

import com.gmiedlar.moviereviewer.domain.ConfirmationToken;
import com.gmiedlar.moviereviewer.domain.CustomUser;
import com.gmiedlar.moviereviewer.repository.ConfirmationTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepository repository;

    public ConfirmationTokenServiceImpl(ConfirmationTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public ConfirmationToken createToken(CustomUser user) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, user);
        return repository.save(confirmationToken);
    }

    @Override
    public ConfirmationToken getConfirmationToken(String token) {
        return repository.findByToken(token);
    }

    @Override
    public ConfirmationToken confirmToken(String token) {
        ConfirmationToken confirmationToken = repository.findByToken(token);
        if(confirmationToken==null) {
            throw new IllegalArgumentException("Invalid token.");
        }
        Calendar cal = Calendar.getInstance();
        if ((confirmationToken.getExpirationDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new IllegalArgumentException("Token have expired.");
        }

        return confirmationToken;
    }
}
