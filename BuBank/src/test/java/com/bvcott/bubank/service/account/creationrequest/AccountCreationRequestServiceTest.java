package com.bvcott.bubank.service.account.creationrequest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bvcott.bubank.dto.account.creationrequest.AccountCreationRequestDTO;
import com.bvcott.bubank.dto.account.creationrequest.AdminCommentDTO;
import com.bvcott.bubank.model.account.creationrequest.AccountCreationRequest;
import com.bvcott.bubank.model.account.creationrequest.RequestStatus;
import com.bvcott.bubank.model.user.Admin;
import com.bvcott.bubank.repository.account.creationrequest.AccountCreationRequestRepository;
import com.bvcott.bubank.repository.user.AdminRepository;
import com.bvcott.bubank.service.AccountService;

class AccountCreationRequestServiceTest {

    @Mock
    private AccountCreationRequestRepository requestRepo;

    @Mock
    private AdminRepository adminRepo;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountCreationRequestService accountCreationRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRequests() {
        when(requestRepo.findAll()).thenReturn(Collections.emptyList());
        assertTrue(accountCreationRequestService.getAllRequests().isEmpty());
        verify(requestRepo, times(1)).findAll();
    }

    @Test
    void testAddCommentToCreationRequest() {
        Long requestId = 1L;
        String username = "admin";
        AdminCommentDTO commentDTO = new AdminCommentDTO();
        commentDTO.setComment("Test comment");

        Admin admin = new Admin();
        admin.setUsername(username);

        AccountCreationRequest request = new AccountCreationRequest();
        request.setStatus(RequestStatus.PENDING);

        when(adminRepo.findByUsername(username)).thenReturn(Optional.of(admin));
        when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));
        when(requestRepo.save(any(AccountCreationRequest.class))).thenReturn(request);

        AccountCreationRequestDTO result = accountCreationRequestService.addCommentToCreationRequest(requestId, commentDTO, username);

        assertNotNull(result);
        verify(adminRepo, times(1)).findByUsername(username);
        verify(requestRepo, times(1)).findById(requestId);
        verify(requestRepo, times(1)).save(any(AccountCreationRequest.class));
    }

    @Test
    void testGetRequestById() {
        Long requestId = 1L;
        AccountCreationRequest request = new AccountCreationRequest();

        when(requestRepo.findById(requestId)).thenReturn(Optional.of(request));

        AccountCreationRequestDTO result = accountCreationRequestService.getRequestById(requestId);

        assertNotNull(result);
        verify(requestRepo, times(1)).findById(requestId);
    }

}