package com.bvcott.bubank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bvcott.bubank.model.Role;
import com.bvcott.bubank.model.User;
import com.bvcott.bubank.repository.UserRepository;

public class UserServiceTest {
    @Mock private UserRepository userRepo;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_registerUser_Success() {
        String username = "testUser";
        String rawPassword = "password";
        Role role = Role.ROLE_USER;
        
        when(userRepo.findByUsername(username)).thenReturn(null);
        when(passwordEncoder.encode(rawPassword)).thenReturn("hashedPassword");
        when(userRepo.save(any(User.class))).thenAnswer(invocation
            -> invocation.getArgument(0));

        User result = userService.registerUser(username, rawPassword, role);

        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("hashedPassword", result.getPassword());
        assertEquals(role, result.getRole());

        verify(userRepo).findByUsername(username);
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepo).save(any(User.class));
    }

    @Test
    void test_registerUser_usernameAlreadyExists() {
        String username = "testUser";
        String rawPassword = "password";
        Role role = Role.ROLE_USER;

        when(userRepo.findByUsername(username)).thenReturn(new User(username, "existingPassword", role));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> 
            userService.registerUser(username, rawPassword, role));

        assertEquals("Username already exists!", ex.getMessage());
        verify(userRepo).findByUsername(username);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    void test_findByUsername_userExists() {
        String username = "testUser";
        User user = new User(username, "password", Role.ROLE_USER);

        when(userRepo.findByUsername(username)).thenReturn(user);

        User result = userService.findByUsername(username);

        assertNotNull(result);
        assertEquals(user, result);

        verify(userRepo).findByUsername(username);
    }

    @Test
    void test_findByUsername_userDoesNotExist() {
        String username = "nonExistentUser";

        when(userRepo.findByUsername(username)).thenReturn(null);

        User result = userService.findByUsername(username);

        assertNull(result);
        verify(userRepo).findByUsername(username);
    }

    @Test
    void testLoadUserByUsername_UserExists() {
        String username = "testUser";
        String password = "hashedPassword";
        Role role = Role.ROLE_USER;
        User user = new User(username, password, role);

        when(userRepo.findByUsername(username)).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(role.name())));

        verify(userRepo).findByUsername(username);
    }

    @Test
    void test_loadUserByUsername_userDoesNotExist() {
        String username = "nonExistentUser";

        when(userRepo.findByUsername(username)).thenReturn(null);

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class, () -> 
            userService.loadUserByUsername(username)
        );

        assertEquals("User not found", ex.getMessage());
        verify(userRepo).findByUsername(username);
    }

    
}
