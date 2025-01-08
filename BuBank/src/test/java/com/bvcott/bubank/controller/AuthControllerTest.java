package com.bvcott.bubank.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bvcott.bubank.dto.LoginSignupDTO;
import com.bvcott.bubank.model.Role;
import com.bvcott.bubank.model.User;
import com.bvcott.bubank.service.UserService;
import com.bvcott.bubank.util.JwtUtil;

public class AuthControllerTest {
    @Mock private UserService userService;
    @Mock private JwtUtil jwtUtil;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_Success() {
        LoginSignupDTO dto = new LoginSignupDTO("testUser", "password");
        User mockUser = new User("testUser", "hashedPassword", Role.ROLE_USER);

        when(userService.registerUser(dto.getUsername(), dto.getPassword(), Role.ROLE_USER)).thenReturn(mockUser);

        String response = authController.register(dto);

        assertEquals("User registered successfully!", response);
        verify(userService).registerUser(dto.getUsername(), dto.getPassword(), Role.ROLE_USER);
    }

    @Test
    void test_login_success() {
        LoginSignupDTO dto = new LoginSignupDTO("testUser", "password");
        User mockUser = new User("testUser", "hashedPassword", Role.ROLE_USER);

        when(userService.findByUsername(dto.getUsername())).thenReturn(mockUser);
        when(passwordEncoder.matches(dto.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(mockUser.getUsername())).thenReturn("mockJwtToken");

        String token = authController.login(dto);

        assertEquals("mockJwtToken", token);
        verify(userService).findByUsername(dto.getUsername());
        verify(passwordEncoder).matches(dto.getPassword(), mockUser.getPassword());
        verify(jwtUtil).generateToken(mockUser.getUsername());
    }

    @Test
    void test_login_userNotFound() {
        LoginSignupDTO dto = new LoginSignupDTO("nonExistingUser", "password");

        when(userService.findByUsername(dto.getUsername())).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authController.login(dto));

        assertEquals("Invalid credentials!", ex.getMessage());
        verify(userService).findByUsername(dto.getUsername());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void test_login_passwordMismatch() {
        LoginSignupDTO dto = new LoginSignupDTO();
        User mockUser = new User("testUser", "hashedPassword", Role.ROLE_USER);

        when(userService.findByUsername(dto.getUsername())).thenReturn(mockUser);
        when(passwordEncoder.matches(dto.getPassword(), mockUser.getPassword())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authController.login(dto));

        verify(userService).findByUsername(dto.getUsername());
        verify(passwordEncoder).matches(dto.getPassword(), mockUser.getPassword());
        verify(jwtUtil, never()).generateToken(anyString());
    }
}
