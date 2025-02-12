package com.bvcott.bubank.controller.account.creationrequest;

import com.bvcott.bubank.dto.CreateAccountDTO;
import com.bvcott.bubank.dto.account.creationrequest.AccountCreationRequestDTO;
import com.bvcott.bubank.dto.account.creationrequest.AdminCommentDTO;
import com.bvcott.bubank.model.account.creationrequest.AccountCreationRequest;
import com.bvcott.bubank.model.account.creationrequest.RequestStatus;
import com.bvcott.bubank.service.account.creationrequest.AccountCreationRequestService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/api/v1/account-requests")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AccountCreationRequestController {
    private final AccountCreationRequestService requestService;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public AccountCreationRequestController(AccountCreationRequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public ResponseEntity<List<AccountCreationRequestDTO>> getAllAccountRequests() {
        List<AccountCreationRequest> requests = requestService.getAllRequests();
        List<AccountCreationRequestDTO> dtos = requests.stream()
                .map(AccountCreationRequestDTO::new)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/comment/{requestId}")
    public ResponseEntity<AccountCreationRequestDTO> addComment(
            @PathVariable Long requestId,
            @RequestBody @Valid AdminCommentDTO adminCommentDTO) {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        AccountCreationRequestDTO updatedRequest = requestService.addCommentToCreationRequest(requestId, adminCommentDTO, username);

        return ResponseEntity.ok(updatedRequest);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<AccountCreationRequestDTO> getRequestById(@PathVariable Long requestId) {
        AccountCreationRequestDTO requestDTO = requestService.getRequestById(requestId);
        return ResponseEntity.ok(requestDTO);
    }

    @PostMapping("/{requestId}")
    public ResponseEntity<AccountCreationRequestDTO> updateRequestDecision(
            @PathVariable Long requestId,
            @RequestParam RequestStatus newStatus,
            @RequestBody CreateAccountDTO dto) {
        log.info("updateRequestDecision triggered with values: requestId: {} , newStatus {}, accountDetails {}", requestId, newStatus, dto);
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        AccountCreationRequestDTO requestDTO = requestService.updateAccountCreationRequestStatus(requestId,
                username,
                newStatus, dto);

        return ResponseEntity.ok(requestDTO);
    }
}
