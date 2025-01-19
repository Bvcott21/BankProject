package com.bvcott.bubank.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bvcott.bubank.dto.LoginSignupDTO;
import com.bvcott.bubank.model.user.User;
import com.bvcott.bubank.service.UserService;
import com.bvcott.bubank.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final PasswordEncoder passwordEncoder;
    
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
        
        User user = userService.registerCustomer(dto.getUsername(), dto.getPassword());

        log.info("Registered user: {}", user);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginSignupDTO dto) {
        log.info("Login triggered with values: {}", dto);

        if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        User user = userService.findByUsername(dto.getUsername());
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            log.error("Invalid credentials!");
            throw new RuntimeException("Invalid credentials!");
        }

        String accessToken = jwtUtil.generateToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        log.info("Password matches, logged in successfully, returning token.");

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken); // Return only the token without "Bearer"
        response.put("refreshToken", refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token is missing"));
        }

        try {
            String username = jwtUtil.extractUsername(refreshToken);
            if(!jwtUtil.validateToken(refreshToken, username)) {
                return ResponseEntity
                        .status(401)
                        .body(Map.of("error", "Invalid refresh token"));
            }
            String newAccessToken = jwtUtil.generateToken(username);
            return ResponseEntity
                    .ok(Map.of("accessToken", newAccessToken));

        } catch (Exception e) {
            return ResponseEntity
                    .status(401)
                    .body(Map.of("error", "Failed to refresh token"));
        }
    }
    
}
