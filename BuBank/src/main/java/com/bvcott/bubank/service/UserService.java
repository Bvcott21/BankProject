package com.bvcott.bubank.service;

import com.bvcott.bubank.model.Role;
import com.bvcott.bubank.model.User;
import com.bvcott.bubank.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
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

//    public User findByUsername(String username) {
//        return userRepository.findByUsername(username);
//    }
}