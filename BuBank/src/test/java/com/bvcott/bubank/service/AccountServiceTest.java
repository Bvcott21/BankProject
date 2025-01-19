package com.bvcott.bubank.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.bvcott.bubank.dto.CreateAccountDTO;
import com.bvcott.bubank.model.user.User;
import com.bvcott.bubank.model.account.Account;
import com.bvcott.bubank.model.account.CheckingAccount;
import com.bvcott.bubank.model.account.SavingsAccount;
import com.bvcott.bubank.repository.AccountRepository;
import com.bvcott.bubank.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
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

    @Test
    void test_listUserAccounts_success() {
        // Arrange: Create a mock user
        User user = new User();
        user.setUserId(1L);
        user.setUsername("testUser");

        // Arrange: Create mock accounts
        CheckingAccount checkingAccount = new CheckingAccount();
        checkingAccount.setId(1L);
        checkingAccount.setAccountNumber("ACC-CHK-00000001");
        checkingAccount.setBalance(BigDecimal.valueOf(1000));
        checkingAccount.setOverdraftLimit(BigDecimal.valueOf(500));

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setId(2L);
        savingsAccount.setAccountNumber("ACC-SAV-00000001");
        savingsAccount.setBalance(BigDecimal.valueOf(2000));
        savingsAccount.setInterestRate(BigDecimal.valueOf(1.5));

        user.setAccounts(List.of(checkingAccount, savingsAccount));

        // Mock repository behavior
        when(userRepo.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Act
        List<Account> result = accountService.listUserAccounts("testUser");

        // Assert
        assertEquals(2, result.size());
        assertEquals("ACC-CHK-00000001", result.get(0).getAccountNumber());
        assertEquals("ACC-SAV-00000001", result.get(1).getAccountNumber());
        assertEquals(BigDecimal.valueOf(1000), result.get(0).getBalance());
        assertEquals(BigDecimal.valueOf(2000), result.get(1).getBalance());

        // Verify interactions
        verify(userRepo, times(1)).findByUsername("testUser");
    }
}