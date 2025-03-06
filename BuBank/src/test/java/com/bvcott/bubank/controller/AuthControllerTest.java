package com.bvcott.bubank.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bvcott.bubank.dto.LoginSignupDTO;
import com.bvcott.bubank.model.user.Customer;
import com.bvcott.bubank.model.user.Role;
import com.bvcott.bubank.model.user.User;
import com.bvcott.bubank.service.UserService;
import com.bvcott.bubank.util.JwtUtil;

class AuthControllerTest {

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

    @SuppressWarnings("null")
    @Test
    void test_login_success() {
        // Arrange
        LoginSignupDTO dto = new LoginSignupDTO("testUser", "password");
        User mockUser = new Customer();
        mockUser.setRole(Role.ROLE_CUSTOMER);
        mockUser.setUsername("testUser");
        mockUser.setPassword("hashedPassword");

        when(userService.findByUsername(dto.getUsername())).thenReturn(mockUser);
        when(passwordEncoder.matches(dto.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(mockUser.getUsername())).thenReturn("mockJwtToken");
        when(jwtUtil.generateRefreshToken(mockUser.getUsername())).thenReturn("mockRefreshToken");

        // Act
        ResponseEntity<Map<String, String>> response = authController.login(dto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("mockJwtToken", response.getBody().get("accessToken"));
        assertEquals("mockRefreshToken", response.getBody().get("refreshToken"));
        verify(userService).findByUsername(dto.getUsername());
        verify(passwordEncoder).matches(dto.getPassword(), mockUser.getPassword());
        verify(jwtUtil).generateToken(mockUser.getUsername());
    }

    @Test
    void test_login_invalidPassword() {
        // Arrange
        LoginSignupDTO dto = new LoginSignupDTO("testUser", "wrongPassword");
        User mockUser = new Customer();
        mockUser.setRole(Role.ROLE_CUSTOMER);
        mockUser.setUsername("testUser");
        mockUser.setPassword("hashedPassword");

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

        when(userService.findByUsername(dto.getUsername())).thenReturn(null); // Ensure `null` is returned

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authController.login(dto));
        assertEquals("Invalid credentials!", exception.getMessage());

        verify(userService).findByUsername(dto.getUsername());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString());
    }

    @Test
    void testRegister() {
        LoginSignupDTO dto = new LoginSignupDTO();
        dto.setUsername("testuser");
        dto.setPassword("password");

        User user = new Customer();
        user.setUsername("testuser");

        when(userService.registerCustomer(dto.getUsername(), dto.getPassword())).thenReturn(user);

        String response = authController.register(dto);

        assertEquals("User registered successfully!", response);
        verify(userService, times(1)).registerCustomer(dto.getUsername(), dto.getPassword());
    }

    @Test
    void testLogin() {
        LoginSignupDTO dto = new LoginSignupDTO();
        dto.setUsername("testuser");
        dto.setPassword("password");

        User user = new Customer();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setRole(Role.ROLE_CUSTOMER);

        when(userService.findByUsername(dto.getUsername())).thenReturn(user);
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getUsername())).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(user.getUsername())).thenReturn("refreshToken");

        ResponseEntity<Map<String, String>> response = authController.login(dto);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("accessToken", response.getBody().get("accessToken"));
        assertEquals("refreshToken", response.getBody().get("refreshToken"));
        assertEquals("testuser", response.getBody().get("username"));
    }

    @Test
    void testRefreshToken() {
        String refreshToken = "validRefreshToken";
        String username = "testuser";

        when(jwtUtil.extractUsername(refreshToken)).thenReturn(username);
        when(jwtUtil.validateToken(refreshToken, username)).thenReturn(true);
        when(jwtUtil.generateToken(username)).thenReturn("newAccessToken");

        ResponseEntity<Map<String, String>> response = authController.refreshToken(Map.of("refreshToken", refreshToken));

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("newAccessToken", response.getBody().get("accessToken"));
    }
}