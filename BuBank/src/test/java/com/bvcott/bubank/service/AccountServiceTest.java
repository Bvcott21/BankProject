package com.bvcott.bubank.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bvcott.bubank.dto.CreateAccountDTO;
import com.bvcott.bubank.dto.CreateAccountRequestDTO;
import com.bvcott.bubank.model.account.Account;
import com.bvcott.bubank.model.account.CheckingAccount;
import com.bvcott.bubank.model.account.SavingsAccount;
import com.bvcott.bubank.model.account.creationrequest.AccountCreationRequest;
import com.bvcott.bubank.model.user.Customer;
import com.bvcott.bubank.model.user.User;
import com.bvcott.bubank.repository.AccountRepository;
import com.bvcott.bubank.repository.account.creationrequest.AccountCreationRequestRepository;
import com.bvcott.bubank.repository.user.CustomerRepository;
import com.bvcott.bubank.repository.user.UserRepository;

public class AccountServiceTest {
    
    @Mock
    private UserRepository userRepo;

    @Mock
    private CustomerRepository customerRepo;

    @Mock
    private AccountRepository accountRepo;

    @Mock
    private AccountCreationRequestRepository requestRepo;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void test_createCheckingAccount_success() {
        // Arrange
        Customer user = new Customer();
        user.setUserId(1L);
        user.setUsername("testUser");

        when(userRepo.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(accountRepo.findTopByAccountNumberStartingWithOrderByIdDesc("ACC-CHK-")).thenReturn(Optional.empty());

        CreateAccountDTO dto = CreateAccountDTO.builder()
                .initialBalance(BigDecimal.valueOf(1000))
                .accountType("checking")
                .overdraftLimit(BigDecimal.valueOf(500))
                .build();

        // Act
        Account account = accountService.createAccount(dto, "testUser");

        // Assert
        assertNotNull(account);
        assertNotNull(account.getAccountNumber());
        assertTrue(account.getAccountNumber().startsWith("ACC-CHK-")); // More flexible check
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
        assertEquals(user, account.getCustomer()); // Use getCustomer() instead of getUser()

        // Verify
        verify(userRepo, times(1)).findByUsername("testUser");
        verify(userRepo, times(1)).save(any(User.class)); // Ensure account is saved
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

        assertEquals("Logged-in user not found.", exception.getMessage());

        verify(userRepo, times(1)).findByUsername("testUser");
        verify(accountRepo, never()).save(any());
    }

    @Test
    public void test_createAccount_invalidAccountType() {
        Customer user = new Customer();
        user.setUserId(1L);
        user.setUsername("testUser");

        when(userRepo.findByUsername("testUser")).thenReturn(Optional.of(user));

        CreateAccountDTO dto = CreateAccountDTO.builder()
                .initialBalance(BigDecimal.valueOf(1000))
                .accountType("invalid")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> accountService.createAccount(dto, "testUser"));

        assertEquals("Invalid account type specified for account number generation.", exception.getMessage());

        verify(userRepo, times(1)).findByUsername("testUser");
        verify(accountRepo, never()).save(any());
    }

    @Test
    void test_listUserAccounts_success() {
        // Arrange: Create a mock customer
        Customer user = new Customer();
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

        user.addAccount(checkingAccount);
        user.addAccount(savingsAccount);

        // Mock repository behavior
        when(userRepo.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Act
        List<Account> result = accountService.listAccounts("testUser");

        // Assert
        assertEquals(2, result.size());
        assertEquals("ACC-CHK-00000001", result.get(0).getAccountNumber());
        assertEquals("ACC-SAV-00000001", result.get(1).getAccountNumber());
        assertEquals(BigDecimal.valueOf(1000), result.get(0).getBalance());
        assertEquals(BigDecimal.valueOf(2000), result.get(1).getBalance());

        // Verify interactions
        verify(userRepo, times(1)).findByUsername("testUser");
    }

    @Test
    void testCreateAccountRequest() {
        CreateAccountRequestDTO dto = CreateAccountRequestDTO.builder()
                .accountType("checking")
                .build();
        Customer customer = new Customer();
        customer.setUsername("testuser");

        when(customerRepo.findByUsername("testuser")).thenReturn(Optional.of(customer));
        when(requestRepo.save(any(AccountCreationRequest.class))).thenAnswer(i -> i.getArguments()[0]);

        AccountCreationRequest request = accountService.createAccountRequest(dto, "testuser");

        assertNotNull(request);
        assertEquals("checking", request.getAccountType());
        assertEquals(customer, request.getRequestedBy());
    }

    @Test
    void testCreateAccount() {
        CreateAccountDTO dto = CreateAccountDTO.builder()
                .accountType("checking")
                .initialBalance(BigDecimal.valueOf(1000))
                .overdraftLimit(BigDecimal.valueOf(500))
                .build();
        User user = new Customer();
        ((Customer) user).setUsername("testuser");

        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(accountRepo.findTopByAccountNumberStartingWithOrderByIdDesc(anyString())).thenReturn(Optional.empty());

        Account account = accountService.createAccount(dto, "testuser");

        assertNotNull(account);
        assertTrue(account instanceof CheckingAccount);
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
        assertEquals(BigDecimal.valueOf(500), ((CheckingAccount) account).getOverdraftLimit());
    }

    @Test
    void testListAccounts() {
        Customer customer = new Customer();
        customer.setUsername("testuser");

        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(customer));

        accountService.listAccounts("testuser");

        verify(userRepo, times(1)).findByUsername("testuser");
    }

    @Test
    void testDeposit() {
        Account account = new CheckingAccount();
        account.setAccountNumber("ACC-CHK-00000001");
        account.setBalance(BigDecimal.valueOf(1000));

        when(accountRepo.findByAccountNumber("ACC-CHK-00000001")).thenReturn(Optional.of(account));

        accountService.deposit("ACC-CHK-00000001", BigDecimal.valueOf(500));

        assertEquals(BigDecimal.valueOf(1500), account.getBalance());
        verify(accountRepo, times(1)).save(account);
    }

    @Test
    void testWithdraw() {
        CheckingAccount account = new CheckingAccount();
        account.setAccountNumber("ACC-CHK-00000001");
        account.setBalance(BigDecimal.valueOf(1000));
        account.setOverdraftLimit(BigDecimal.valueOf(500));

        when(accountRepo.findByAccountNumber("ACC-CHK-00000001")).thenReturn(Optional.of(account));

        accountService.withdraw("ACC-CHK-00000001", BigDecimal.valueOf(500));

        assertEquals(BigDecimal.valueOf(500), account.getBalance());
        verify(accountRepo, times(1)).save(account);
    }

    @Test
    void testFindAccountById() {
        Account account = new CheckingAccount();
        account.setId(1L);

        when(accountRepo.findById(1L)).thenReturn(Optional.of(account));

        Account foundAccount = accountService.findAccountById(1L);

        assertNotNull(foundAccount);
        assertEquals(1L, foundAccount.getId());
    }

    @Test
    void testFindAccountByAccountNumber() {
        Account account = new CheckingAccount();
        account.setAccountNumber("ACC-CHK-00000001");

        when(accountRepo.findByAccountNumber("ACC-CHK-00000001")).thenReturn(Optional.of(account));

        Account foundAccount = accountService.findAccountByAccountNumber("ACC-CHK-00000001");

        assertNotNull(foundAccount);
        assertEquals("ACC-CHK-00000001", foundAccount.getAccountNumber());
    }

    @Test
    void testIsOwner() {
        when(accountRepo.existsByAccountNumberAndCustomer_Username("ACC-CHK-00000001", "testuser")).thenReturn(true);

        boolean isOwner = accountService.isOwner("ACC-CHK-00000001", "testuser");

        assertTrue(isOwner);
    }

    @Test
    void testValidateAccountExists() {
        when(accountRepo.existsByAccountNumber("ACC-CHK-00000001")).thenReturn(true);

        assertDoesNotThrow(() -> accountService.validateAccountExists("ACC-CHK-00000001"));
    }
}