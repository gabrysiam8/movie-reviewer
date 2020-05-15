package com.gmiedlar.moviereviewer.service;

import com.gmiedlar.moviereviewer.dto.PasswordDto;
import com.gmiedlar.moviereviewer.dto.UserDto;

public interface UserService {
    UserDto getUserByUsername(String username);
    UserDto getUserById(String id);
    String updateUserPassword(String username, PasswordDto passwords);
    String resetUserPassword(String id, PasswordDto passwords);
    String deleteUser(String username);
}
