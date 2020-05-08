package com.gmiedlar.moviereviewer.service;

import java.util.Collections;
import java.util.List;

import com.gmiedlar.moviereviewer.domain.CustomUser;
import com.gmiedlar.moviereviewer.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService, UserFinderService {

    private final UserRepository repository;

    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUser user = repository.findByEmail(username)
                                    .or(() -> repository.findByUsername(username))
                                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String role = user.getRole();
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
        return new User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public CustomUser findUserByUsername(String username) {
        return repository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("No user with that email or username exists!"));
    }
}
