package com.bvcott.accountservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bvcott.accountservice.dto.AccountCreationRequestDTO;
import com.bvcott.accountservice.dto.AdminCommentDTO;
import com.bvcott.accountservice.dto.CreateAccountDTO;
import com.bvcott.accountservice.model.AccountCreationRequest;
import com.bvcott.accountservice.model.RequestStatus;
import com.bvcott.accountservice.service.AccountCreationRequestService;

import jakarta.validation.Valid;

@RestController @RequestMapping("/api/v1/account-requests")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AccountCreationRequestController {
    private final AccountCreationRequestService requestService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public AccountCreationRequestController(AccountCreationRequestService requestService) {
        this.requestService = requestService;
    }

    // @GetMapping
    // public ResponseEntity<List<AccountCreationRequestDTO>> getAllAccountRequests() {
    //     List<AccountCreationRequest> requests = requestService.getAllRequests();
    //     List<AccountCreationRequestDTO> dtos = requests.stream()
    //             .map(AccountCreationRequestDTO::new)
    //             .toList();
    //     return ResponseEntity.ok(dtos);
    // }

    // @PostMapping("/comment/{requestId}")
    // public ResponseEntity<AccountCreationRequestDTO> addComment(
    //         @PathVariable Long requestId,
    //         @RequestBody @Valid AdminCommentDTO adminCommentDTO) {
    //     String username = SecurityContextHolder
    //             .getContext()
    //             .getAuthentication()
    //             .getName();

    //     AccountCreationRequestDTO updatedRequest = requestService.addCommentToCreationRequest(requestId, adminCommentDTO, username);

    //     return ResponseEntity.ok(updatedRequest);
    // }

    // @GetMapping("/{requestId}")
    // public ResponseEntity<AccountCreationRequestDTO> getRequestById(@PathVariable Long requestId) {
    //     AccountCreationRequestDTO requestDTO = requestService.getRequestById(requestId);
    //     return ResponseEntity.ok(requestDTO);
    // }

    // @PostMapping("/{requestId}")
    // public ResponseEntity<AccountCreationRequestDTO> updateRequestDecision(
    //         @PathVariable Long requestId,
    //         @RequestParam RequestStatus newStatus,
    //         @RequestBody CreateAccountDTO dto) {
    //     log.info("updateRequestDecision triggered with values: requestId: {} , newStatus {}, accountDetails {}", requestId, newStatus, dto);
    //     String username = SecurityContextHolder
    //             .getContext()
    //             .getAuthentication()
    //             .getName();

    //     AccountCreationRequestDTO requestDTO = requestService.updateAccountCreationRequestStatus(requestId,
    //             username,
    //             newStatus, dto);

    //     return ResponseEntity.ok(requestDTO);
    // }
}
