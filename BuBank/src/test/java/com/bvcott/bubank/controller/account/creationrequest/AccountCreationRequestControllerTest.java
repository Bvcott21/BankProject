package com.bvcott.bubank.controller.account.creationrequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bvcott.bubank.dto.CreateAccountDTO;
import com.bvcott.bubank.dto.account.creationrequest.AccountCreationRequestDTO;
import com.bvcott.bubank.dto.account.creationrequest.AdminCommentDTO;
import com.bvcott.bubank.model.account.creationrequest.AccountCreationRequest;
import com.bvcott.bubank.model.account.creationrequest.RequestStatus;
import com.bvcott.bubank.service.account.creationrequest.AccountCreationRequestService;

class AccountCreationRequestControllerTest {

    @Mock
    private AccountCreationRequestService requestService;

    @InjectMocks
    private AccountCreationRequestController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // Mock SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllAccountRequests() throws Exception {
        when(requestService.getAllRequests()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/account-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddComment() throws Exception {
        AdminCommentDTO commentDTO = new AdminCommentDTO();
        AccountCreationRequest request = new AccountCreationRequest();
        AccountCreationRequestDTO requestDTO = new AccountCreationRequestDTO(request);
        when(requestService.addCommentToCreationRequest(anyLong(), any(AdminCommentDTO.class), any(String.class)))
                .thenReturn(requestDTO);

        mockMvc.perform(post("/api/v1/account-requests/comment/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comment\":\"Test comment\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetRequestById() throws Exception {
        AccountCreationRequest request = new AccountCreationRequest();
        AccountCreationRequestDTO requestDTO = new AccountCreationRequestDTO(request);
        when(requestService.getRequestById(anyLong())).thenReturn(requestDTO);

        mockMvc.perform(get("/api/v1/account-requests/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateRequestDecision() throws Exception {
        CreateAccountDTO createAccountDTO = CreateAccountDTO.builder().build();
        AccountCreationRequest request = new AccountCreationRequest();
        AccountCreationRequestDTO requestDTO = new AccountCreationRequestDTO(request);
        when(requestService.updateAccountCreationRequestStatus(anyLong(), any(String.class), any(RequestStatus.class), any(CreateAccountDTO.class)))
                .thenReturn(requestDTO);

        mockMvc.perform(post("/api/v1/account-requests/1")
                        .param("newStatus", "APPROVED")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }
}
