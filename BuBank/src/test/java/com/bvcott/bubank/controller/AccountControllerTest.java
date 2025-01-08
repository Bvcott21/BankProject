package com.bvcott.bubank.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bvcott.bubank.dto.CreateAccountDTO;
import com.bvcott.bubank.filter.JwtFilter;
import com.bvcott.bubank.model.account.BusinessAccount;
import com.bvcott.bubank.model.account.CheckingAccount;
import com.bvcott.bubank.model.account.SavingsAccount;
import com.bvcott.bubank.service.AccountService;
import com.bvcott.bubank.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private AccountController accountController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the ApplicationContext for JwtFilter
        when(applicationContext.getBean(UserDetailsService.class)).thenReturn(userDetailsService);

        // Create a JwtFilter instance with mocks
        JwtFilter jwtFilter = new JwtFilter(jwtUtil);
        jwtFilter.setApplicationContext(applicationContext);

        // Set up MockMvc with JwtFilter
        mockMvc = MockMvcBuilders.standaloneSetup(accountController)
                .addFilters(jwtFilter) // Include JwtFilter
                .build();
    }

    @Test
    void test_createCheckingAccount_success() throws Exception {
        // Arrange: Create input DTO
        CreateAccountDTO dto = CreateAccountDTO.builder()
                .initialBalance(BigDecimal.valueOf(1000))
                .accountType("checking")
                .overdraftLimit(BigDecimal.valueOf(500))
                .build();

        // Arrange: Mock expected account
        CheckingAccount account = new CheckingAccount();
        account.setId(1L);
        account.setAccountNumber("ACC-CHK-00000001");
        account.setBalance(BigDecimal.valueOf(1000));
        account.setOverdraftLimit(BigDecimal.valueOf(500));

        // Arrange: Mock token and username extraction
        String mockToken = "Bearer mockJwtToken";
        String username = "testUser";
        when(jwtUtil.extractUsername("mockJwtToken")).thenReturn(username);

        // Mock user details for JwtFilter
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken("mockJwtToken", username)).thenReturn(true);

        // Arrange: Mock service behavior
        when(accountService.createAccount(dto, username)).thenReturn(account);

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/create")
                        .header("Authorization", mockToken) // Pass "Bearer" prefixed token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("ACC-CHK-00000001"))
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.overdraftLimit").value(500));

        // Verify interactions
        verify(jwtUtil, times(1)).extractUsername("mockJwtToken");
        verify(accountService, times(1)).createAccount(dto, username);
    }

    @Test
    void test_createSavingsAccount_success() throws Exception {
        CreateAccountDTO dto = CreateAccountDTO.builder()
                .initialBalance(BigDecimal.valueOf(2000))
                .accountType("savings")
                .interestRate(BigDecimal.valueOf(1.5))
                .build();

        SavingsAccount account = new SavingsAccount();
        account.setId(1L);
        account.setAccountNumber("ACC-SAV-00000001");
        account.setBalance(BigDecimal.valueOf(2000));
        account.setInterestRate(BigDecimal.valueOf(1.5));

        String mockToken = "Bearer mockJwtToken";
        String username = "testUser";

        when(jwtUtil.extractUsername("mockJwtToken")).thenReturn(username);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken("mockJwtToken", username)).thenReturn(true);
        when(accountService.createAccount(dto, username)).thenReturn(account);

        mockMvc.perform(post("/api/v1/accounts/create")
                        .header("Authorization", mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("ACC-SAV-00000001"))
                .andExpect(jsonPath("$.balance").value(2000))
                .andExpect(jsonPath("$.interestRate").value(1.5));

        verify(jwtUtil, times(1)).extractUsername("mockJwtToken");
        verify(accountService, times(1)).createAccount(dto, username);
    }

    @Test
    void test_createBusinessAccount_success() throws Exception {
        CreateAccountDTO dto = CreateAccountDTO.builder()
                .initialBalance(BigDecimal.valueOf(5000))
                .accountType("business")
                .creditLimit(BigDecimal.valueOf(10000))
                .build();

        BusinessAccount account = new BusinessAccount();
        account.setId(3L);
        account.setAccountNumber("ACC-BUS-00000001");
        account.setBalance(BigDecimal.valueOf(5000));
        account.setCreditLimit(BigDecimal.valueOf(10000));

        String mockToken = "Bearer mockJwtToken";
        String username = "testUser";

        when(jwtUtil.extractUsername("mockJwtToken")).thenReturn(username);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken("mockJwtToken", username)).thenReturn(true);
        when(accountService.createAccount(dto, username)).thenReturn(account);

        mockMvc.perform(post("/api/v1/accounts/create")
                        .header("Authorization", mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.accountNumber").value("ACC-BUS-00000001"))
                .andExpect(jsonPath("$.balance").value(5000))
                .andExpect(jsonPath("$.creditLimit").value(10000));

        verify(jwtUtil, times(1)).extractUsername("mockJwtToken");
        verify(accountService, times(1)).createAccount(dto, username);
    }

    @Test
    void test_missingAuthorizationHeader() throws Exception {
        CreateAccountDTO dto = CreateAccountDTO.builder()
                .initialBalance(BigDecimal.valueOf(1000))
                .accountType("checking")
                .build();

        mockMvc.perform(post("/api/v1/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(accountService);
    }

    @Test
    void test_invalidToken() throws Exception {
        CreateAccountDTO dto = CreateAccountDTO.builder()
                .initialBalance(BigDecimal.valueOf(1000))
                .accountType("checking")
                .build();

        String mockToken = "Bearer invalidToken";
        when(jwtUtil.extractUsername("invalidToken")).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(post("/api/v1/accounts/create")
                        .header("Authorization", mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        verify(jwtUtil, times(1)).extractUsername("invalidToken");
        verifyNoInteractions(accountService);
    }

    @Test
    void test_invalidAccountType() throws Exception {
        CreateAccountDTO dto = CreateAccountDTO.builder()
                .initialBalance(BigDecimal.valueOf(1000))
                .accountType("invalid")
                .build();

        String mockToken = "Bearer mockJwtToken";
        String username = "testUser";

        when(jwtUtil.extractUsername("mockJwtToken")).thenReturn(username);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken("mockJwtToken", username)).thenReturn(true);

        when(accountService.createAccount(dto, username)).thenThrow(new IllegalArgumentException("Invalid account type"));

        mockMvc.perform(post("/api/v1/accounts/create")
                        .header("Authorization", mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());

        verify(jwtUtil, times(1)).extractUsername("mockJwtToken");
        verify(accountService, times(1)).createAccount(dto, username);
    }
}