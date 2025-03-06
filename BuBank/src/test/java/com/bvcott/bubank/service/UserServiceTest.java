package com.bvcott.bubank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.bvcott.bubank.model.user.Customer;
import com.bvcott.bubank.model.user.Role;
import com.bvcott.bubank.model.user.User;
import com.bvcott.bubank.repository.user.UserRepository;

public class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void test_findByUsername_userExists() {
        User user = new Customer();
        user.setUsername("testUser");

        when(userRepo.findByUsername("testUser")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("testUser");

        assertNotNull(result);
        assertEquals(user, result);

        verify(userRepo).findByUsername("testUser");
    }

    @Test
    void test_registerCustomer_usernameExists() {
        String username = "existingUser";
        String rawPassword = "password";

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(new Customer()));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.registerCustomer(username, rawPassword);
        });

        assertEquals("Username already exists!", exception.getMessage());

        verify(userRepo).findByUsername(username);
        verify(userRepo, never()).save(any(Customer.class));
    }

    @Test
    void test_loadUserByUsername_userExists() {
        String username = "existingUser";
        String password = "password";
        Role role = Role.ROLE_CUSTOMER;

        User user = new Customer();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role.name())));

        verify(userRepo).findByUsername(username);
    }

    @Test
    void test_loadUserByUsername_userNotFound() {
        String username = "nonExistingUser";

        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(username);
        });

        assertEquals("Username not found with the provided username", exception.getMessage());

        verify(userRepo).findByUsername(username);
    }
}
