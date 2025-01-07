package com.bvcott.bubank.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bvcott.bubank.dto.CreateAccountDTO;
import com.bvcott.bubank.model.Account;
import com.bvcott.bubank.model.User;
import com.bvcott.bubank.repository.UserRepository;

public class AccountServiceTest {
    @Mock UserRepository userRepo;
    @InjectMocks private AccountService accountService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test public void test_createAccount_success() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testUser");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        CreateAccountDTO dto = CreateAccountDTO
            .builder()
            .userId(1L)
            .initialBalance(BigDecimal.valueOf(1000))
            .build();
        
        Account account = accountService.createAccount(dto);

        assertNotNull(account);
        assertNotNull(account.getAccountNumber());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
        assertEquals(user, account.getUser());

        verify(userRepo, times(1)).save(user);
    }

    @Test
    void test_createAccount_userNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        CreateAccountDTO dto = CreateAccountDTO
            .builder()
            .userId(1L)
            .initialBalance(BigDecimal.valueOf(1000))
            .build();
        
        // Execute service method and verify exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            accountService.createAccount(dto));
            assertEquals("User not found.", exception.getMessage());

        verify(userRepo, never()).save(any());
    }

}
