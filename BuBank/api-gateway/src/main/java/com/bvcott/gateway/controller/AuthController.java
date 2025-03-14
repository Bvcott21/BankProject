package com.bvcott.gateway.controller;

import com.bvcott.gateway.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final JwtUtil jwtUtil;
    private final WebClient webClient; // to call the user service
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(JwtUtil jwtUtil, WebClient.Builder webClientBuilder) {
        this.jwtUtil = jwtUtil;
        // Configure the web client to point to your user service base URL.
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Validate credentials by calling the user service's login endpoint.
        // This should return user details if credentials are valid.
        Map<String, Object> userResponse = webClient.post()
                .uri("/api/v1/auth/login")
                .bodyValue(credentials)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (userResponse == null || !userResponse.containsKey("username")) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }

        // Generate token with gateway's JwtUtil
        String token = jwtUtil.generateToken(username);
        log.info("Generated JWT token for user: {}", username);
        return ResponseEntity.ok(Map.of("accessToken", token));
    }
}
