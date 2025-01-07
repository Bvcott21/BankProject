package com.bvcott.bubank.service;

import com.bvcott.bubank.model.Role;
import com.bvcott.bubank.model.User;
import com.bvcott.bubank.repository.UserRepository;

import java.util.Collections;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String rawPassword, Role role) {
        if (userRepository.findByUsername(username) != null) {
            throw new RuntimeException("Username already exists!");
        }
        String hashedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(username, hashedPassword, role);
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return org.springframework.security.core.userdetails.User
            .builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.singletonList(() -> 
                user.getRole().name())
            )
            .build();
    }

    
}