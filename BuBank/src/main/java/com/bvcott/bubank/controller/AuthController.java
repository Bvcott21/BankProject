package com.bvcott.bubank.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bvcott.bubank.model.Role;
import com.bvcott.bubank.model.User;
import com.bvcott.bubank.service.UserService;
import com.bvcott.bubank.util.JwtUtil;

@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    
    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password) {
        userService.registerUser(username, password, Role.ROLE_USER);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        User user = userService.findByUsername(username);

        if(user == null || !user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid credentials!");
        }
        
        return jwtUtil.generateToken(user.getUsername());
    }
    
}
