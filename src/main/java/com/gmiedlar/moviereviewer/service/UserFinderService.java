package com.gmiedlar.moviereviewer.service;

import com.gmiedlar.moviereviewer.domain.CustomUser;

public interface UserFinderService {
    CustomUser findUserByUsername(String username);
}
