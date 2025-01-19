package com.bvcott.bubank.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.bvcott.bubank.dto.LoginSignupDTO;
import com.bvcott.bubank.model.user.Role;
import com.bvcott.bubank.model.user.User;
import com.bvcott.bubank.service.UserService;
import com.bvcott.bubank.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

public class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_login_success() {
        // Arrange
        LoginSignupDTO dto = new LoginSignupDTO("testUser", "password");
        User mockUser = new User("testUser", "hashedPassword", Role.ROLE_USER);

        when(userService.findByUsername(dto.getUsername())).thenReturn(mockUser);
        when(passwordEncoder.matches(dto.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(mockUser.getUsername())).thenReturn("mockJwtToken");

        // Act
        ResponseEntity<Map<String, String>> response = authController.login(dto);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("mockJwtToken", response.getBody().get("token"));
        verify(userService).findByUsername(dto.getUsername());
        verify(passwordEncoder).matches(dto.getPassword(), mockUser.getPassword());
        verify(jwtUtil).generateToken(mockUser.getUsername());
    }

    @Test
    void test_login_invalidPassword() {
        // Arrange
        LoginSignupDTO dto = new LoginSignupDTO("testUser", "wrongPassword");
        User mockUser = new User("testUser", "hashedPassword", Role.ROLE_USER);

        when(userService.findByUsername(dto.getUsername())).thenReturn(mockUser);
        when(passwordEncoder.matches(dto.getPassword(), mockUser.getPassword())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authController.login(dto));
        assertEquals("Invalid credentials!", exception.getMessage());

        verify(userService).findByUsername(dto.getUsername());
        verify(passwordEncoder).matches(dto.getPassword(), mockUser.getPassword());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void test_login_userNotFound() {
        // Arrange
        LoginSignupDTO dto = new LoginSignupDTO("nonExistingUser", "password");

        when(userService.findByUsername(dto.getUsername())).thenReturn(null);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authController.login(dto));
        assertEquals("Invalid credentials!", exception.getMessage());

        verify(userService).findByUsername(dto.getUsername());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString());
    }
}
