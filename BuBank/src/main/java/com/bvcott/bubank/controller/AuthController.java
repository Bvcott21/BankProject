package com.bvcott.bubank.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bvcott.bubank.dto.LoginSignupDTO;
import com.bvcott.bubank.model.Role;
import com.bvcott.bubank.model.User;
import com.bvcott.bubank.service.UserService;
import com.bvcott.bubank.util.JwtUtil;

@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private PasswordEncoder passwordEncoder;
    
    public AuthController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostMapping("/register")
    public String register(@RequestBody LoginSignupDTO dto) {
        log.info("Register triggered with values: {}", dto);

        if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        
        User user = userService.registerUser(dto.getUsername(), dto.getPassword(), Role.ROLE_USER);

        log.info("Registered user: {}", user);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginSignupDTO dto) {
        log.info("Login triggered with values: {}", dto);
        log.info("Finding user by username");
        User user = userService.findByUsername(dto.getUsername());

        if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if(user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            log.error("Passwords don't match, throwing exception...");
            throw new RuntimeException("Invalid credentials!");
        }
        
        String token = jwtUtil.generateToken(user.getUsername());
        log.info("Password matches, logged in successfully, returning token: {}", token);
        return token;
    }
    
}
