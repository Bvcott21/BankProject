package com.bvcott.bubank.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bvcott.bubank.dto.CreateAccountDTO;
import com.bvcott.bubank.model.User;
import com.bvcott.bubank.model.account.Account;
import com.bvcott.bubank.model.account.CheckingAccount;
import com.bvcott.bubank.repository.AccountRepository;
import com.bvcott.bubank.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

public class AccountServiceTest {
    @Mock
    private UserRepository userRepo;

    @Mock
    private AccountRepository accountRepo;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_createCheckingAccount_success() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testUser");

        when(userRepo.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(accountRepo.findTopByAccountNumberStartingWithOrderByIdDesc("ACC-CHK-")).thenReturn(Optional.empty());

        CreateAccountDTO dto = CreateAccountDTO.builder()
                .initialBalance(BigDecimal.valueOf(1000))
                .accountType("checking")
                .overdraftLimit(BigDecimal.valueOf(500))
                .build();

        Account account = accountService.createAccount(dto, "testUser");

        assertNotNull(account);
        assertNotNull(account.getAccountNumber());
        assertEquals("ACC-CHK-00000001", account.getAccountNumber());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
        assertEquals(user, account.getUser());

        verify(userRepo, times(1)).findByUsername("testUser");
        verify(userRepo, times(1)).save(user);
    }

    @Test
    public void test_createAccount_userNotFound() {
        when(userRepo.findByUsername("testUser")).thenReturn(Optional.empty());

        CreateAccountDTO dto = CreateAccountDTO.builder()
                .initialBalance(BigDecimal.valueOf(1000))
                .accountType("checking")
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.createAccount(dto, "testUser"));

        verify(userRepo, times(1)).findByUsername("testUser");
        verify(userRepo, never()).save(any());
    }

    @Test
    public void test_createAccount_invalidAccountType() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testUser");

        when(userRepo.findByUsername("testUser")).thenReturn(Optional.of(user));

        CreateAccountDTO dto = CreateAccountDTO.builder()
                .initialBalance(BigDecimal.valueOf(1000))
                .accountType("invalid")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.createAccount(dto, "testUser"));

        verify(userRepo, times(1)).findByUsername("testUser");
        verify(userRepo, never()).save(any());
    }
}