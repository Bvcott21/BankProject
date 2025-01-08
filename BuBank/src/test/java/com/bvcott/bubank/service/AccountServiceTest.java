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

import com.bvcott.bubank.model.account.BusinessAccount;
import com.bvcott.bubank.model.account.CheckingAccount;
import com.bvcott.bubank.model.account.SavingsAccount;
import com.bvcott.bubank.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bvcott.bubank.dto.CreateAccountDTO;
import com.bvcott.bubank.model.account.Account;
import com.bvcott.bubank.model.User;
import com.bvcott.bubank.repository.UserRepository;

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

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepo.findTopByAccountNumberStartingWithOrderByIdDesc("ACC-CHK-"))
                .thenReturn(Optional.empty());

        CreateAccountDTO dto = CreateAccountDTO.builder()
                .userId(1L)
                .initialBalance(BigDecimal.valueOf(1000))
                .accountType("checking")
                .overdraftLimit(BigDecimal.valueOf(500))
                .build();

        Account account = accountService.createAccount(dto);

        assertNotNull(account);
        assertNotNull(account.getAccountNumber());
        assertEquals("ACC-CHK-00000001", account.getAccountNumber());
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
        assertEquals(user, account.getUser());

        // Verify interactions
        verify(userRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).save(user);
        verify(accountRepo, never()).save(any());
    }

    @Test
    public void test_createSavingsAccount_success() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testUser");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepo.findTopByAccountNumberStartingWithOrderByIdDesc("ACC-SAV-"))
                .thenReturn(Optional.empty());

        CreateAccountDTO dto = CreateAccountDTO.builder()
                .userId(1L)
                .initialBalance(BigDecimal.valueOf(2000))
                .accountType("savings")
                .interestRate(BigDecimal.valueOf(1.5))
                .build();

        Account account = accountService.createAccount(dto);

        assertNotNull(account);
        assertNotNull(account.getAccountNumber());
        assertEquals("ACC-SAV-00000001", account.getAccountNumber());
        assertEquals(BigDecimal.valueOf(2000), account.getBalance());
        assertEquals(user, account.getUser());

        // Verify interactions
        verify(userRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).save(user);
        verify(accountRepo, never()).save(any());
    }

    @Test
    public void test_createBusinessAccount_success() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testUser");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(accountRepo.findTopByAccountNumberStartingWithOrderByIdDesc("ACC-BUS-"))
                .thenReturn(Optional.empty());

        CreateAccountDTO dto = CreateAccountDTO.builder()
                .userId(1L)
                .initialBalance(BigDecimal.valueOf(5000))
                .accountType("business")
                .creditLimit(BigDecimal.valueOf(10000))
                .build();

        Account account = accountService.createAccount(dto);

        assertNotNull(account);
        assertNotNull(account.getAccountNumber());
        assertEquals("ACC-BUS-00000001", account.getAccountNumber());
        assertEquals(BigDecimal.valueOf(5000), account.getBalance());
        assertEquals(user, account.getUser());

        // Verify interactions
        verify(userRepo, times(1)).findById(1L);
        verify(userRepo, times(1)).save(user);
        verify(accountRepo, never()).save(any());
    }

    @Test
    public void test_createAccount_userNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        CreateAccountDTO dto = CreateAccountDTO.builder()
                .userId(1L)
                .initialBalance(BigDecimal.valueOf(1000))
                .accountType("checking")
                .build();

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.createAccount(dto));
        assertEquals("User not found.", exception.getMessage());

        // Verify no interactions with save methods
        verify(userRepo, times(1)).findById(1L);
        verify(userRepo, never()).save(any());
        verify(accountRepo, never()).save(any());
    }

    @Test
    public void test_createAccount_invalidAccountType() {
        User user = new User();
        user.setUserId(1L);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        CreateAccountDTO dto = CreateAccountDTO.builder()
                .userId(1L)
                .initialBalance(BigDecimal.valueOf(1000))
                .accountType("invalid-type")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.createAccount(dto));

        // Verify no interactions with save methods
        verify(userRepo, times(1)).findById(1L);
        verify(userRepo, never()).save(any());
        verify(accountRepo, never()).save(any());
    }

}
