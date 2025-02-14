package com.bvcott.bubank.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bvcott.bubank.dto.CreateAccountRequestDTO;
import com.bvcott.bubank.filter.JwtFilter;
import com.bvcott.bubank.model.account.creationrequest.AccountCreationRequest;
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

import java.util.List;

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

        when(applicationContext.getBean(UserDetailsService.class)).thenReturn(userDetailsService);

        JwtFilter jwtFilter = new JwtFilter(jwtUtil);
        jwtFilter.setApplicationContext(applicationContext);

        mockMvc = MockMvcBuilders.standaloneSetup(accountController)
                .addFilters(jwtFilter)
                .build();
    }

    @Test
    void test_createAccountRequest_success() throws Exception {
        // Arrange: Create input DTO with only `accountType`
        CreateAccountRequestDTO dto = CreateAccountRequestDTO.builder()
                .accountType("checking")
                .build();

        // Arrange: Mock expected response
        AccountCreationRequest accountRequest = new AccountCreationRequest();
        accountRequest.setRequestId(1L); // Ensure correct field

        String mockToken = "Bearer mockJwtToken"; // Ensure correct format
        String username = "testUser";

        // Mock JwtUtil behavior correctly
        when(jwtUtil.extractUsername("mockJwtToken")).thenReturn(username);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken("mockJwtToken", username)).thenReturn(true);
        when(accountService.createAccountRequest(dto, username)).thenReturn(accountRequest);

        // Act & Assert
        mockMvc.perform(post("/api/v1/accounts/create-request")
                        .header("Authorization", mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(1L)); // Ensure correct JSON field

        verify(jwtUtil, times(1)).extractUsername("mockJwtToken");
        verify(accountService, times(1)).createAccountRequest(dto, username);
    }

    @Test
    void test_getUserAccounts_success() throws Exception {
        String mockToken = "Bearer mockJwtToken";
        String username = "testUser";

        when(jwtUtil.extractUsername("mockJwtToken")).thenReturn(username);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken("mockJwtToken", username)).thenReturn(true);
        when(accountService.listAccounts(username)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/accounts")
                        .header("Authorization", mockToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(jwtUtil, times(1)).extractUsername("mockJwtToken");
        verify(accountService, times(1)).listAccounts(username);
    }

    @Test
    void test_missingAuthorizationHeader() throws Exception {
        CreateAccountRequestDTO dto = CreateAccountRequestDTO.builder()
                .accountType("checking")
                .build();

        mockMvc.perform(post("/api/v1/accounts/create-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(accountService);
    }

    @Test
    void test_invalidToken() throws Exception {
        CreateAccountRequestDTO dto = CreateAccountRequestDTO.builder()
                .accountType("checking")
                .build();

        String mockToken = "Bearer invalidToken";
        when(jwtUtil.extractUsername("invalidToken")).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(post("/api/v1/accounts/create-request")
                        .header("Authorization", mockToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        verify(jwtUtil, times(1)).extractUsername("invalidToken");
        verifyNoInteractions(accountService);
    }
}