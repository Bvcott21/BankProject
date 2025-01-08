package com.bvcott.bubank.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bvcott.bubank.dto.CreateAccountDTO;
import com.bvcott.bubank.model.account.Account;
import com.bvcott.bubank.model.account.CheckingAccount;
import com.bvcott.bubank.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    void test_createCheckingAccount_success() throws Exception {
        CreateAccountDTO dto = CreateAccountDTO.builder()
                .userId(1L)
                .initialBalance(BigDecimal.valueOf(1000))
                .accountType("checking")
                .overdraftLimit(BigDecimal.valueOf(500))
                .build();

        CheckingAccount account = new CheckingAccount();
        account.setId(1L);
        account.setAccountNumber("ACC-CHK-00000001");
        account.setBalance(BigDecimal.valueOf(1000));
        account.setOverdraftLimit(BigDecimal.valueOf(500));

        when(accountService.createAccount(dto)).thenReturn(account);

        mockMvc.perform(post("/api/v1/accounts/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("ACC-CHK-00000001"))
                .andExpect(jsonPath("$.balance").value(1000))
                .andExpect(jsonPath("$.overdraftLimit").value(500));

        verify(accountService, times(1)).createAccount(dto);
    }

    @Test
    void test_createSavingsAccount_success() throws Exception {
        CreateAccountDTO dto = CreateAccountDTO.builder()
                .userId(1L)
                .initialBalance(BigDecimal.valueOf(2000))
                .accountType("savings")
                .interestRate(BigDecimal.valueOf(1.5))
                .build();

        Account account = new Account();
        account.setId(1L);
        account.setAccountNumber("ACC-SAV-00000001");
        account.setBalance(BigDecimal.valueOf(2000));

        when(accountService.createAccount(dto)).thenReturn(account);

        mockMvc.perform(post("/api/v1/accounts/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("ACC-SAV-00000001"))
                .andExpect(jsonPath("$.balance").value(2000));

        verify(accountService, times(1)).createAccount(dto);
    }

    @Test
    void test_createAccount_userNotFound() throws Exception {
        CreateAccountDTO dto = CreateAccountDTO.builder()
                .userId(1L)
                .initialBalance(BigDecimal.valueOf(1000))
                .accountType("checking")
                .build();

        when(accountService.createAccount(dto)).thenThrow(new RuntimeException("User not found."));

        mockMvc.perform(post("/api/v1/accounts/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("User not found."));

        verify(accountService, times(1)).createAccount(dto);
    }

    @Test
    void test_createAccount_invalidAccountType() throws Exception {
        CreateAccountDTO dto = CreateAccountDTO.builder()
                .userId(1L)
                .initialBalance(BigDecimal.valueOf(1000))
                .accountType("invalid-type")
                .build();

        when(accountService.createAccount(dto)).thenThrow(new IllegalArgumentException("Invalid account type specified."));

        mockMvc.perform(post("/api/v1/accounts/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());

        verify(accountService, times(1)).createAccount(dto);
    }
}