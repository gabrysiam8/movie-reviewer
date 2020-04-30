package com.gmiedlar.moviereviewer.service;

import com.gmiedlar.moviereviewer.domain.ConfirmationToken;
import com.gmiedlar.moviereviewer.domain.CustomUser;

public interface ConfirmationTokenService {

    ConfirmationToken createToken(CustomUser user);
    ConfirmationToken getConfirmationToken(String token);
    ConfirmationToken confirmToken(String token);
}
