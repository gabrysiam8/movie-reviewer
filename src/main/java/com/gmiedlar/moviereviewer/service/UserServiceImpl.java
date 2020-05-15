package com.gmiedlar.moviereviewer.service;

import com.gmiedlar.moviereviewer.domain.CustomUser;
import com.gmiedlar.moviereviewer.dto.PasswordDto;
import com.gmiedlar.moviereviewer.dto.UserDto;
import com.gmiedlar.moviereviewer.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto getUserByUsername(String username) {
        CustomUser user = repository.findByUsername(username)
                                    .orElseThrow(() -> new UsernameNotFoundException("No user with that username exists!"));

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());

        return userDto;
    }

    @Override
    public UserDto getUserById(String id) {
        CustomUser user = repository.findById(id)
                                    .orElseThrow(() -> new IllegalArgumentException("No user with that id exists!"));

        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());

        return userDto;
    }

    @Override
    public String updateUserPassword(String username, PasswordDto passwords) {
        CustomUser userUpdate = repository.findByUsername(username)
                                          .orElseThrow(() -> new UsernameNotFoundException("No user with that email or username exists!"));

        if(!passwordEncoder.matches(passwords.getOldPassword(), userUpdate.getPassword()))
            throw new IllegalArgumentException("Wrong password!");
        if(!passwords.getNewPassword().equals(passwords.getPasswordConfirmation()))
            throw new IllegalArgumentException("The Password confirmation must match New password!");
        userUpdate.setPassword(passwordEncoder.encode(passwords.getNewPassword()));
        repository.save(userUpdate);
        return "Password successfully changed";
    }

    @Override
    public String resetUserPassword(String id, PasswordDto passwords) {
        CustomUser userUpdate = repository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No user with that id exists!"));

        if(!passwords.getNewPassword().equals(passwords.getPasswordConfirmation()))
            throw new IllegalArgumentException("The Password confirmation must match New password!");
        userUpdate.setPassword(passwordEncoder.encode(passwords.getNewPassword()));
        repository.save(userUpdate);
        return "Password successfully changed";
    }

    @Override
    public String deleteUser(String username) {
        CustomUser user = repository.findByUsername(username)
                                    .orElseThrow(() -> new UsernameNotFoundException("No user with that email or username exists!"));

        repository.delete(user);
        return "Account successfully deleted";
    }
}
