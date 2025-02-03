package com.bvcott.bubank.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bvcott.bubank.model.user.User;
import com.bvcott.bubank.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

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
        User user = new User();
        user.setUsername("testUser");

        when(userRepo.findByUsername("testUser")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("testUser");

        assertNotNull(result);
        assertEquals(user, result);

        verify(userRepo).findByUsername("testUser");
    }
}